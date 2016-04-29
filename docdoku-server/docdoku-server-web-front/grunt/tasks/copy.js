var moduleName = 'copy';

module.exports = {

    name:moduleName,

    loadConf:function(config,grunt){

        config.copy.libs =  {
            files: [
                {
                    expand: true,
                    dot: false,
                    cwd: 'app',
                    dest: 'dist',
                    src: [
                        'bower_components/requirejs/require.js',
                        'bower_components/modernizr/modernizr.js',
                        'bower_components/jquery/jquery.min.*',
                        'bower_components/underscore/underscore-min.js',
                        'bower_components/threejs/build/three.min.js',
                        'bower_components/tweenjs/src/Tween.js',
                        'bower_components/bootstrap/docs/assets/js/bootstrap.min.js',
                        'bower_components/backbone/backbone-min.js'
                    ]
                }
            ]
        };

        config.copy.assets = {
            files: [
                {
                    expand: true,
                    dot: false,
                    cwd: 'app',
                    dest: 'dist',
                    src: [
                        'css/**',
                        'images/**',
                        'sounds/**',
                        'fonts/**',
                        'download/**',
                        'js/home/main.js',
                        'js/lib/plugin-detect.js',
                        'js/lib/empty.pdf',
                        'js/lib/charts/**'
                    ]
                },{
                    expand: true,
                    dot: false,
                    cwd: 'app/bower_components/bootstrap/',
                    dest: 'dist',
                    src: [
                        'img/*'
                    ]
                }
            ]
        };
        config.copy.dmu = {
            files: [
                {
                    expand: true,
                    dot: false,
                    cwd: 'app',
                    dest: 'dist',
                    src: [
                        /*
                         * worker utils
                         * */
                        'js/product-structure/workers/*',
                        'js/product-structure/dmu/loaders/*',
                        'js/product-structure/dmu/utils/*',
                        'js/product-structure/dmu/controls/*',
                        'js/product-structure/permalinkApp.js'
                    ]
                }
            ]
        };
        config.copy.i18n ={
            files: [
                {
                    expand: true,
                    dot: false,
                    cwd: 'app',
                    dest: 'dist',
                    src: [
                        'js/localization/nls/*',
                        'js/localization/nls/fr/*',
                        'js/localization/nls/es/*'
                    ]
                }
            ]
        };
    },

    loadTasks:function(grunt){
    }

};