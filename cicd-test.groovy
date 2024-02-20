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
                    [key: 'pr_url', value: '\$.pull_request.url', defaultValue:''],
                    [key: 'pr_head_branch', value: '\$.pull_request.head.ref', defaultValue:''],
                ],
                causeString: "$object_kind" == "tag" ? 'Triggered By Github On tag: $tag' : 'Triggered By Github On Pull Request: $pr_url',
                genericRequestVariables: [],
                genericHeaderVariables: [],
                token: "abcdfed",
                tokenCredentialId: '',
                printContributedVariables: false,
                printPostContent: false,
                silentResponse: false,
                shouldNotFlattern: false,
                regexpFilterText: '$object_kind $tag $pr_action $pr_head_branch',
                regexpFilterExpression: '(^tag\\s\\d+\\.\\d+\\.\\d+)|((opened|synchronize)\\sdev$)'
        )
    }
    // triggers {
    //     GenericTrigger(
    //         genericVariables:[
    //                 [key: 'project', value:"\$.project.name", defaultValue:''],
    //                 [key: 'tag', value:"\$.ref", regexpFilter:'^[^\\/]+\\/([^\\/]+)\\/', defaultValue:''],
    //                 [key: 'checkout_sha', value: '\$.checkout_sha', defaultValue:''],
    //                 [key: 'commit', value:"\$.commits.message", defaultValue:''],
    //                 [key: 'object_kind', value: '\$.object_kind', defaultValue:''],
    //                 [key: 'last_commit', value: '\$.object_attributes.last_commit.id', defaultValue:''],
    //                 [key: 'target_branch', value: '\$.object_attributes.target_branch', defaultValue:''],
    //                 [key: 'merge_status', value: '\$.object_attributes.detailed_merge_status', defaultValue:''],
    //                 [key: 'merge_action', value: '\$.object_attributes.action', defaultValue:''],
    //             ],
    //             causeString: 'Triggered By Gitlab On $tag',
    //             genericRequestVariables: [],
    //             genericHeaderVariables: [],
    //             token: "gahsia8xuQuaeceighie9doo",
    //             tokenCredentialId: '',
    //             printContributedVariables: false,
    //             printPostContent: false,
    //             silentResponse: false,
    //             shouldNotFlattern: false,
    //             regexpFilterText: '$object_kind $tag $checkout_sha $last_commit $target_branch $merge_status $merge_action',
    //             regexpFilterExpression: '(^tag_push\\s\\d+\\.\\d+\\.\\d+-alpha[.-]?\\d*\\s.{40}\\s\\s\\s\\s$)|(^merge_request\\s\\s\\s.{40}\\sdev\\smergeable\\sopen$)'
    //     )
    // }
    
    stages {
        stage('Hello') {
            steps {
                echo '$project - $object_kind $tag'
            }
        }
    }
}
