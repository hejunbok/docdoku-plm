(function(){

    'use strict';

    angular.module('dplm.services.3d',[])

        .factory('AvailableLoaders',function(){
            return ['js','json','obj','stl','dae','ply','wrl','bin'];
        })

        .service('ModelLoaderService',function($q){

            // Returns a THREE.Mesh in a promise

            this.load = function(filename){

                var fs = require('fs');
                var deferred = $q.defer();
                var extension = filename.split('.').pop();
                var material = new THREE.MeshNormalMaterial();

                function mergeToSingleGeometry(object){
                    var geometries = [];
                    object.traverse(function(child){
                        if (child instanceof THREE.Mesh && child.geometry) {
                            geometries.push(child.geometry);
                        }
                    });
                    var combined = new THREE.Geometry();
                    angular.forEach(geometries,function(geometry){
                        THREE.GeometryUtils.merge(combined, geometry);
                    });
                    return combined;
                }

                switch (extension){
                    case 'obj':

                        fs.readFile(filename, 'utf-8' ,function (err, data) {
                            if (err) deferred.reject(err);
                            else{
                                var loader = new THREE.OBJLoader();
                                var object = loader.parse(data);
                                var geometry = mergeToSingleGeometry(object);
                                deferred.resolve(new THREE.Mesh(geometry, material));
                            }
                        });

                        break;

                    case 'stl':

                        fs.readFile(filename, 'binary', function (err, data) {
                            if (err) deferred.reject(err);
                            else{
                                var loader = new THREE.STLLoader();
                                var geometry = loader.parse(data);
                                deferred.resolve(new THREE.Mesh(geometry,material));
                            }
                        });

                        break;

                    case 'wrl':

                        fs.readFile(filename, 'utf-8', function (err, data) {
                            if (err) deferred.reject(err);
                            else{
                                var loader = new THREE.VRMLLoader();
                                var object = loader.parse(data);
                                var geometry = mergeToSingleGeometry(object);
                                deferred.resolve(new THREE.Mesh(geometry, material));
                            }
                        });

                        break;

                    case 'bin':

                        fs.readFile(filename, 'binary', function (err, data) {
                            if (err) deferred.reject(err);
                            else{
                                var loader = new THREE.BinaryLoader();
                                var str = data.toString();
                                var buffer = new ArrayBuffer( str.length );
                                var bufView = new Uint8Array( buffer );
                                for ( var i = 0, l = str.length; i < l; i ++ ) {
                                    bufView[ i ] = str.charCodeAt( i ) & 0xff;
                                }
                                loader.createBinModel( buffer, function(geometry){
                                    deferred.resolve(new THREE.Mesh(geometry, material));
                                }, '', {} );
                            }
                        });

                        break;

                    case 'dae':

                        fs.readFile(filename, 'utf-8', function (err, data) {
                            if (err) deferred.reject(err);
                            else{
                                var loader = new THREE.ColladaLoader();
                                var xmlParser = new DOMParser();
                                var doc = xmlParser.parseFromString( data, "application/xml" );
                                loader.parse( doc, function(object){
                                    var geometry = mergeToSingleGeometry(object);
                                    deferred.resolve(new THREE.Mesh(geometry, material));
                                });
                            }
                        });

                        break;

                    case 'js':
                    case 'json':

                        fs.readFile(filename, 'utf-8', function (err, data) {
                            if (err) deferred.reject(err);
                            else{
                                var loader = new THREE.JSONLoader();
                                var object = loader.parse(JSON.parse(data),'');
                                deferred.resolve(new THREE.Mesh(object.geometry, material));
                            }
                        });

                        break;

                    case 'ply':

                        fs.readFile(filename, 'binary',  function (err, data) {
                            if (err) deferred.reject(err);
                            else{
                                var loader = new THREE.PLYLoader();
                                var str = data.toString();
                                var buffer = new ArrayBuffer( str.length );
                                var bufView = new Uint8Array( buffer );
                                for ( var i = 0, l = str.length; i < l; i ++ ) {
                                    bufView[ i ] = str.charCodeAt( i ) & 0xff;
                                }
                                var geometry = loader.parse(buffer);
                                deferred.resolve(new THREE.Mesh(geometry, material));
                            }
                        });

                        break;

                    default :
                        deferred.reject();
                        break;
                }

                return deferred.promise;

            };

        })

        .directive('modelViewer',function(ModelLoaderService,$filter){

            return {
                restrict: 'A',
                scope: {
                    'width': '=',
                    'height': '=',
                    'fillcontainer': '=',
                    'filename':'='
                },

                link: function postLink(scope, element, attrs) {

                    var camera, scene, renderer,
                        light, controls,
                        contW = (scope.fillcontainer) ? element[0].clientWidth : scope.width,
                        contH = scope.height,
                        windowHalfX = contW / 2,
                        windowHalfY = contH / 2,
                        destroy = false,
                        spin = document.createElement('i');

                    spin.classList.add('fa');
                    spin.classList.add('fa-spinner');
                    spin.classList.add('fa-spin');

                    element[0].appendChild(spin);

                    var onWindowResize = function () {
                        contW = (scope.fillcontainer) ?
                            element[0].clientWidth : scope.width;
                        contH = scope.height;

                        windowHalfX = contW / 2;
                        windowHalfY = contH / 2;

                        camera.aspect = contW / contH;
                        camera.updateProjectionMatrix();

                        renderer.setSize( contW, contH );
                    };

                    var onMeshLoaded = function(mesh){

                        element[0].removeChild(spin);
                        scene.add( mesh );
                        mesh.geometry.computeBoundingBox();
                        var center = mesh.geometry.boundingBox.center();
                        controls.target.copy(center);
                        camera.position.copy(center);
                        camera.position.z -= mesh.geometry.boundingBox.size().length() * 2;
                        animate();
                    };

                    var onError = function(){
                        element[0].removeChild(spin);
                        destroy = true;
                    };

                    var init = function () {
                        camera = new THREE.PerspectiveCamera( 20, contW / contH, 1, 1000000 );
                        camera.position.z = 1800;
                        scene = new THREE.Scene();
                        light = new THREE.DirectionalLight( 0xffffff );
                        light.position.set( 0, 0, 1 );
                        scene.add( light );
                        var canvas = document.createElement( 'canvas' );
                        canvas.width = 128;
                        canvas.height = 128;
                        renderer = new THREE.WebGLRenderer( { antialias: true , alpha:true} );
                        renderer.setClearColor( 0x000000, 0 );
                        renderer.setSize( contW, contH );
                        element[0].appendChild( renderer.domElement );
                        controls = new THREE.OrbitControls( camera,element[0]);
                        window.addEventListener( 'resize', onWindowResize, false );
                    };

                    var animate = function () {
                        if(!destroy){
                            requestAnimationFrame(animate);
                        }
                        controls.update();
                        renderer.render( scene, camera );
                    };

                    scope.$watch('fillcontainer + width + height', onWindowResize);

                    scope.$on('$destroy',function(){
                        destroy = true;
                    });

                    init();
                    ModelLoaderService.load(scope.filename).then(onMeshLoaded,onError);

                }
            };
        });
})();
