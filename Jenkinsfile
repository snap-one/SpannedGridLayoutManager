#!groovy

// Publishes the SpannedGridLayoutManager AAR to Artifactory. All internal details (repo URLs,
// credentials, JDK selection) live in the C4Jenkins shared library step, so this public repo
// carries no internal hostnames or secrets.
//
// This is a single (non-multibranch) pipeline meant to be run manually with "Build with
// Parameters" — appropriate for a library that publishes only a couple of times a year:
//   - Leave RELEASE unticked -> publishes a -SNAPSHOT to the snapshot repo (for testing).
//   - Tick RELEASE           -> publishes an immutable release (e.g. 4.1.1) to the release repo.
//     Bump `baseVersion` in spannedgridlayoutmanager/build.gradle.kts FIRST — a release version
//     cannot be republished once it exists.
@Library('C4Jenkins@develop') _

pipeline {
    // 'Android' is the shared pool label Phoenix uses for its build and "Deploy libraries to
    // artifactory" stages — any free Android-capable agent (JDK 21 + Android SDK) can run this.
    agent { label 'Android' }

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    parameters {
        booleanParam(
            name: 'RELEASE',
            defaultValue: false,
            description: 'Tick to publish an immutable release to c4-component-release-local ' +
                '(bump baseVersion in build.gradle.kts first). Leave unticked to publish a ' +
                '-SNAPSHOT to c4-component-snapshot-local for testing.'
        )
    }

    stages {
        stage('Publish to Artifactory') {
            steps {
                publishSpannedGridLayoutManager(release: params.RELEASE)
            }
        }
    }

    post {
        success {
            echo "✅ SpannedGridLayoutManager published (${params.RELEASE ? 'RELEASE' : 'SNAPSHOT'})"
        }
        failure {
            echo '❌ SpannedGridLayoutManager publish pipeline failed'
        }
    }
}
