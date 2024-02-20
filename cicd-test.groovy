pipeline {
    // Define Agents & Pod Template
    agent {
        kubernetes {
            cloud 'kubernetes'
            inheritFrom 'default image-builder golang ci-test'
        }
    }

    // Define Parameters Input
    parameters {
        string(name: 'tag', defaultValue: '', description: 'Checkout Tag - Required', trim: true)
        hidden(name: 'commit', defaultValue: '', description: 'Hidden parameters')
        hidden(name: 'object_kind', defaultValue: 'manual_trigger', description: 'Hidden parameters')
        booleanParam(name: 'Skip_TestStage', defaultValue: true, description: '')
        hidden(name: 'checkout_sha', defaultValue: '', description: 'Hidden parameters')
    }

    // Define Options Setting
    options {
        ansiColor('xterm')
        skipDefaultCheckout(true)
    }

    // Define Trigger Parameters
    triggers {
        GenericTrigger(
            genericVariables:[
                    [key: 'project', value:"\$.repository.name", defaultValue:''],
                    [key: 'object_kind', value: '\$.ref_type', defaultValue:''],
                    [key: 'tag', value:"\$.ref", defaultValue:''],
                    [key: 'pr_action', value: '\$.action', defaultValue:''],
                    [key: 'pr_url', value: '\$.pull_request.html_url', defaultValue:''],
                    [key: 'pr_head_branch', value: '\$.pull_request.head.ref', defaultValue:''],
                ],
                causeString: 'Triggered By Github: $tag$pr_url',
                genericRequestVariables: [],
                genericHeaderVariables: [],
                token: "abcdfed",
                tokenCredentialId: '',
                printContributedVariables: false,
                printPostContent: false,
                silentResponse: false,
                shouldNotFlattern: false,
                regexpFilterText: '$object_kind $tag $pr_action $pr_head_branch',
                regexpFilterExpression: '(^tag\\s\\d+\\.\\d+\\.\\d+-alpha[.-]?\\d*)|((opened|synchronize)\\sdev$)'
        )
    }
    
    stages {
        // Stage for checkout SCM
        stage('Checkout') {
            // Using Git to checkout to specific tag
            steps {
                script {
                    if ("$object_kind" == 'tag' || "$object_kind" == "manual_trigger") {
                        if("$tag" ==~ /\d+\.\d+\.\d+-alpha[.-]?\d*/) {
                            echo 'Tag format is validated'
                            echo ("$object_kind" == 'tag' || "$object_kind" == "manual_trigger" ? "$tag" : "$last_commit")
                        } else {
                            error "Tag is not in a standard format."                                 
                        }
                    }

                    // Clone the repository and checkout to the specific tag
                    deleteDir()
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: ("$object_kind" == 'tag' || "$object_kind" == "manual_trigger" ? "$tag" : "$last_commit")]],
                        doGenerateSubmoduleConfigurations: false,
                        extensions: [
                            [$class: 'CleanBeforeCheckout'],
                            [$class: 'ChangelogToBranch',options:[compareRemote: 'origin' ,compareTarget: 'develop' ]]
                        ],
                        userRemoteConfigs: [[
                            url: "git@github.com:WaiShen1000/cicd-test.git"
                        ]]
                    ])

                    // get checkout_sha when manual_trigger
                    if ("$object_kind" == 'manual_trigger' && "$checkout_sha" == '') {
                        checkout_sha = sh(script: "git log --format=%H -n 1",  returnStdout: true).trim()
                    }

                    sh "mkdir -p build/reports"
                }
            }
        }

        stage('CDCD'){
            when {
                environment name: 'object_kind', value: 'tag' 
            }

            steps {
                echo '$project - $object_kind $tag'
            }
        }
    }
}
