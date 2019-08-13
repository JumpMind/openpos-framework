module.exports = function(grunt) {

    grunt.initConfig({
        clean: {
            build: ['dist/openpos-client-core-lib/*']
        },
        concurrent: {
            target: {
                tasks: ['watch', 'watch-bundle', 'watch-build'],
                options: {
                    logConcurrentOutput: true
                }
            },
            withTests: {
                tasks: ['watch-bundle', 'watch-build', 'test', 'watch'],
                options: {
                    logConcurrentOutput: true
                }
            }
        },
        exec: {
            link: {
                options: {
                    cwd: 'dist/openpos-client-core-lib',
                },
                cmd: 'npm link'
            },
            install: {
                cmd: 'npm install'
            },
            build: {
                cmd: 'npm run ng build -- --watch'
            },
            bundle: {
                cmd: 'npm run scss-bundle -- -c projects/openpos-client-core-lib/scss-bundle.config.json --watch projects/openpos-client-core-lib/src'
            },
            test: {
                cmd: 'npm test -- --karmaConfig=projects/openpos-client-core-lib/karma.conf.dev.js'
            }
        },
        mkdir: {
            all: {
              options: {
                create: ['dist/openpos-client-core-lib']
              },
            },
        },
        watch: {
            package: {
              files: [
                  'dist/openpos-client-core-lib/package.json'
                ],
              tasks: ['exec:link'],
              options: {
                event: ['changed'],
                spawn: false,
              },
            },
          },
    });

    grunt.loadNpmTasks('grunt-concurrent');
    grunt.loadNpmTasks('grunt-exec');
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-mkdir');
    grunt.loadNpmTasks('grunt-contrib-watch');

    grunt.registerTask('default', ['exec:install', 'clean', 'mkdir', 'create-package', 'concurrent:target']);
    grunt.registerTask('withTests', ['exec:install', 'clean', 'mkdir', 'create-package', 'concurrent:withTests'])
    grunt.registerTask('watch-build', ['exec:build']);
    grunt.registerTask('watch-bundle', ['exec:bundle']);
    grunt.registerTask('test', ['exec:test']);
    
    grunt.registerTask('wait', 'wait:pause', function() {
        var done = this.async();
        setTimeout(function() {
            done();
          }, 60000);
    });
    grunt.registerTask('npm-link', ['wait', 'exec:link'])
    grunt.registerTask('create-package', 'Creates an empty package.json', function() {
        grunt.file.write('dist/openpos-client-core-lib/package.json', '');
     });
  };