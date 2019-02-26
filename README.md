## AWS (serverless) application template

A template for a (ClojureScript, Lambda) Serverless function with
[CodePipeline][codepipeline]-based CI/CD from GitHub that includes build, test
and deployment to integration test and production environments.

## Prerequisites

The CodePipeline configuration assumes that the account has been
[bootstrapped][bootstrap] to provide the deployment artefact S3 buckets and
base IAM policies.

## Usage

Add a [GitHub personal access token][accesstoken] to the [Parameter
Store][paramstore], make appropriate changes to [the pipeline
configuration][config] to provide access to the GitHub source, ...

```
  make pipeline
```

... and the Pipeline will do the rest.

## License

Published under the [2-clause BSD license][license]

[codepipeline]: https://aws.amazon.com/codepipeline/
[bootstrap]: https://github.com/sinistral/aws-slave-infra
[accesstoken]: https://help.github.com/en/articles/creating-a-personal-access-token-for-the-command-line
[paramstore]: https://docs.aws.amazon.com/systems-manager/latest/userguide/systems-manager-paramstore.html
[config]: https://github.com/sinistral/aws-serverless-application-template/blob/master/cloudformation/ssapp-pipeline/template/ssapp-pipeline.yaml

[license]: https://opensource.org/licenses/BSD-2-Clause
