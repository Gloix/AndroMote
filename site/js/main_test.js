var MAX_SPEED = 20, MIN_SPEED=20;

var infoDiv;

var scene, camera, renderer, sphere;
var windowHalfX, windowHalfY;
var customUniforms;
var clock;

var lastMouseX, lastMouseY;

//Key states
var key_up = false;
var key_down = false;
var key_left = false;
var key_right = false;

//Speed at which the player moves through space.
var speed = 5;

//The box.
var box;

//Material for the box.
var boxMaterial;

$(function() {
	infoDiv = $('div#info');
	//$("#shaders textarea").allowTabChar();
	/*$('#shaders textarea#fragment').bind('input propertychange', function() {
		sphereMaterial.fragmentShader = this.value;
		sphereMaterial.needsUpdate = true;
	});
	
	$('#shaders textarea#vertex').bind('input propertychange', function() {
		sphereMaterial.vertexShader = this.value;
		sphereMaterial.needsUpdate = true;
	});*/
	
	clock = new THREE.Clock();
	// set the scene size
	var WIDTH = 400,
	  HEIGHT = 300;

	// set some camera attributes
	var VIEW_ANGLE = 80,
	  ASPECT = WIDTH / HEIGHT,
	  NEAR = 0.1,
	  FAR = 1000000;

	// get the DOM element to attach to
	// - assume we've got jQuery to hand
	var $container = $('#container');
	
	// create a WebGL renderer, camera
	// and a scene
	renderer = new THREE.WebGLRenderer();
	camera =
	  new THREE.PerspectiveCamera(
		VIEW_ANGLE,
		ASPECT,
		NEAR,
		FAR);

	scene = new THREE.Scene();
	// add the camera to the scene
	scene.add(camera);

	// the camera starts at 0,0,0
	// so pull it back
	camera.position.z = 15;
	
	/*var urlPrefix = "images/skybox/";
	var urlSuffix = ".jpg";
	var urls = ["posx", "negx", "posy", "negy", "posz", "negz"];
	
	skyGeometry = new THREE.CubeGeometry( 500000, 500000, 500000);
	
	materialArray=[];
	for(var i=0;i<6;i++) {
		materialArray.push(
			new THREE.MeshBasicMaterial(
				{map:THREE.ImageUtils.loadTexture(urlPrefix+urls[i]+urlSuffix),
				side: THREE.BackSide
				}
			)
		);
	}
	var skyMaterial = new THREE.MeshFaceMaterial( materialArray );
	var skyBox = new THREE.Mesh( skyGeometry, skyMaterial );
	scene.add(skyBox);*/
	
	//////////////////////////////////////////////////////////////
	// set up the sphere vars
	/*var radius = 50,
		segments = 16,
		rings = 16;*/
	
	// create the box's material
	boxMaterial =
	  new THREE.MeshLambertMaterial(
		{
			color: 0xCC0000
		});
	//customUniforms = {time:{type:'f',value:1.0}};
	/*boxMaterial = new THREE.ShaderMaterial(
		{
			vertexShader: "void main() {gl_Position=projectionMatrix * modelViewMatrix * vec4(position,1.0);}",
			fragmentShader: "uniform float time; void main() {gl_FragColor=vec4(1.0,0.5,1.0,0.0);}"
			//uniforms:customUniforms
		}
	);*/
	
	//boxMaterial = new THREE.MeshBasicMaterial({color:0x00ff00});
	
	var geometry = new THREE.BoxGeometry(9,18,1);
	box = new THREE.Mesh(geometry, boxMaterial);
	scene.add(box);

	// create a new mesh with
	// sphere geometry - we will cover
	// the sphereMaterial next!
	/*geometry = new THREE.SphereGeometry(
		radius,
		segments,
		rings);
	
	for(var i=0;i<50;i++) {
		sphere = new THREE.Mesh(geometry, sphereMaterial);
		
		sphere.position.x = Math.random() * 1000 - 500;
		sphere.position.y = Math.random() * 1000 - 500;
		sphere.position.z = Math.random() * 1000 - 500;
		sphere.force = new THREE.Vector3();
		sphere.velocity = new THREE.Vector3();
		
		// add the sphere to the scene
		scene.add(sphere);
		spheres.push(sphere);
	}*/
	
	//////////////////////////////////////////////////////////////
	
	// create a point light
	var pointLight =
	  new THREE.PointLight(0xFFFFFF);

	// set its position
	pointLight.position.x = 10;
	pointLight.position.y = 50;
	pointLight.position.z = 130;

	// add to the scene
	scene.add(pointLight);
	
	var ambientLight = 
		new THREE.AmbientLight(0x484848);
		
	scene.add(ambientLight);
	
	//////////////////////////////////////////////////////////////

	// start the renderer
	renderer.setSize(WIDTH, HEIGHT);

	// attach the render-supplied DOM element
	$container.append(renderer.domElement);
	
	
	/*$container.on("mousedown",function(event) {
		lastMouseX = event.offsetX;
		lastMouseY = event.offsetY;
		dragging = true;
	});
	
	$container.on("mouseup",function(event) {
		dragging = true;
	});*/
	
	
	
	var sensivity = 500;
	var MOVESPEED = 5;
	var LOOKSPEED = 5;
	// Camera moves with mouse, flies around with WASD/arrow keys
	//controls = new THREE.FirstPersonControls(camera); // Handles camera control
	//controls.movementSpeed = MOVESPEED; // How fast the player can walk around
	//controls.lookSpeed = LOOKSPEED; // How fast the player can look around with the mouse
	
	var dragging = false;
	
	$container.on("mouseup", function(event)  {
		dragging = false;
	});
	
	$container.on("mousedown", function(event)  {
		dragging = true;
		lastMouseX = event.offsetX;
		lastMouseY = event.offsetY;
	});
	//camera.rotation.x = -0.57;
	camera.rotation.order="YXZ";
	$container.on("mousemove", function(event)  {
		if(dragging) {
			var x = camera.rotation.x;
			camera.rotateX(-x);
			camera.rotateY((lastMouseX-event.offsetX)/sensivity);
			camera.rotateX(x);
			var diffX = (lastMouseY-event.offsetY)/sensivity;
			if( Math.abs(diffX+x) < Math.PI/2-0.05) {
				camera.rotateX(diffX);
			}
			camera.rotation.z = 0;
			
			lastMouseX = event.offsetX;
			lastMouseY = event.offsetY;
		}
	});
	
	$(document).on("keydown", function(event) {
		switch(event.keyCode) {
		case 69:
			camera.rotateZ(-0.1); break;
		case 88:
			camera.rotateZ(0.1); break;
		case 16:
			speed=MAX_SPEED; break;
		case 65:
			key_left = true; break;
		case 83:
			key_down = true; break;	
		case 68:
			key_right = true; break;
		case 87:
			key_up = true; break;
		}
	});
	
	$(document).on("keyup", function(event) {
		switch(event.keyCode) {
		case 16:
			speed=MIN_SPEED; break;
		case 65:
			key_left = false; break;
		case 83:
			key_down = false; break;	
		case 68:
			key_right = false; break;
		case 87:
			key_up = false; break;
		}
	});
	onResizeWindow();
	$(window).on('resize', onResizeWindow);
	loop();
	
});

var time=0;
var force = 5;

function animate() {
	var dt = 5.1;
	/*for(x in spheres){
		spheres[0].position.x = Math.cos(time)*15;
		spheres[0].position.y = Math.sin(time)*15;
		for(y in spheres){
			if(x > y) {
				
				var thisForceX = force/Math.pow((spheres[x].position.x - spheres[y].position.x),2);
				var thisForceY = force/Math.pow((spheres[x].position.y - spheres[y].position.y),2);
				var thisForceZ = force/Math.pow((spheres[x].position.z - spheres[y].position.z),2);
				//this.ForceX *= (spheres[x].position.x - spheres[y].position.x)
				
				spheres[x].velocity.x += thisForce*dt;
				spheres[x].velocity.y += thisForce*dt;
				spheres[x].velocity.z += thisForce*dt;
				
				spheres[y].velocity.x += thisForce*dt;
				spheres[y].velocity.y += thisForce*dt;
				spheres[y].velocity.z += thisForce*dt;
				
				
				spheres[x].position.x += spheres[x].velocity.x*dt+thisForce*dt*dt;
				spheres[x].position.y += spheres[x].velocity.y*dt+thisForce*dt*dt;
				spheres[x].position.z += spheres[x].velocity.z*dt+thisForce*dt*dt;
				
				spheres[y].position.x += spheres[x].velocity.x*dt+thisForce*dt*dt;
				spheres[y].position.y += spheres[x].velocity.y*dt+thisForce*dt*dt;
				spheres[y].position.z += spheres[x].velocity.z*dt+thisForce*dt*dt;
				
			}
		}
	}*/
	
	time+=0.1;
}

function loop() {
	requestAnimationFrame(loop);
	
	//customUniforms.time.value += clock.getDelta();
	
	animate();
	
	if(key_up) camera.translateZ(-speed);
	if(key_down) camera.translateZ(speed);
	if(key_right) camera.translateX(speed);
	if(key_left) camera.translateX(-speed);
	
	infoDiv.html("Camera:"+
					"<br>x:"+(camera.position.x).toFixed(2)+
					"<br>y:"+(camera.position.y).toFixed(2)+
					"<br>z:"+(camera.position.z).toFixed(2)+
					"<br>rx:"+(camera.rotation.x).toFixed(2)+
					"<br>ry:"+(camera.rotation.y).toFixed(2)+
					"<br>rz:"+(camera.rotation.z).toFixed(2));
	
	renderer.render(scene, camera);
	
}

function onResizeWindow() {
	/*windowHalfX = window.innerWidth / 2;
	windowHalfY = window.innerHeight / 2;

	camera.aspect = window.innerWidth / window.innerHeight;
	camera.updateProjectionMatrix();

	renderer.setSize( window.innerWidth, window.innerHeight );*/
}