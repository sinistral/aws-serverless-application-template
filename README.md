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

... and the Pipeline will do the rest to deploy a so-called "principal"
Pipeline to build, test and deploy into production the application.

It is sometimes useful to deploy an alternate instance of the Pipeline and
application to - for example - verify IAM permissions as the application grows
in complexity and depends on more AWS services.  This is easily achieved by
providing a qualifier and source configuration to distinguish the alternate
deployment from the principal:

```
  make pipeline stack-qualifier=dev-${USER} \
    github-user=sinistral \
    github-repo=aws-serverless-application-template \
    github-branch=develop.alt-stack
```

Clean-up for alternate Pipelines and applications is provided by `destroy-*`
goals:

```
  # tear everything (Pipeline & application) down
  make destroy-pipeline stack-qualifier=dev-${USER}
```

```
  # "Just the apps, ma'am"
  make destroy-application stack-qualifier=dev-${USER}
```


## License

Published under the [2-clause BSD license][license]

[codepipeline]: https://aws.amazon.com/codepipeline/
[bootstrap]: https://github.com/sinistral/aws-slave-infra
[accesstoken]: https://help.github.com/en/articles/creating-a-personal-access-token-for-the-command-line
[paramstore]: https://docs.aws.amazon.com/systems-manager/latest/userguide/systems-manager-paramstore.html
[config]: https://github.com/sinistral/aws-serverless-application-template/blob/master/cloudformation/ssapp-pipeline/template/ssapp-pipeline.yaml

[license]: https://opensource.org/licenses/BSD-2-Clause
