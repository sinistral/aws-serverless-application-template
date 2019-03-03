
application-name       := ssapp
target-dir             := target
templates              := $(shell find cloudformation -path "*/template/*.yaml")
configs                := $(shell find cloudformation -path "*/config/*.json")

validated-templates    := $(patsubst %, $(target-dir)/%, $(templates))
validated-configs      := $(patsubst %, $(target-dir)/%, $(configs))

AWS_REGION             ?= eu-central-1
bootstrap-stack-name   ?= bootstrap

pkgd-pipeline-template := $(target-dir)/cloudformation/$(application-name)-pipeline/template/$(application-name)-pipeline-packaged.yaml
pkgd-pipeline-config   := $(target-dir)/cloudformation/$(application-name)-pipeline/config/config.json

.PHONY clean very-clean:
clean:
	rm -rf target
very-clean: clean
	rm -rf .cljs_node_repl out

$(target-dir):
	mkdir -p $(target-dir)

# CloudFormation template validation

$(target-dir)/%.yaml: %.yaml
	mkdir -p $(@D)
	aws --region ${AWS_REGION} \
		cloudformation validate-template \
		--template-body file://$<
	cp $*.yaml $@

$(target-dir)/%.json: %.json
	mkdir -p $(@D)
	sed -e "s|__DEPLOYMENT_TYPE__|$(deployment-type)|" $< \
		| jq '{ Parameters: [ .[] |  { (.ParameterKey): .ParameterValue }  ] | add } '  \
		| tee $@

.PHONY: cloudformation
cloudformation: $(validated-configs) $(validated-templates)

# Pipeline deployment

$(pkgd-pipeline-template): $(target-dir)/cloudformation/$(application-name)-pipeline/template/$(application-name)-pipeline.yaml
	aws --region $(AWS_REGION) \
		cloudformation package \
		--s3-bucket $(shell aws --region $(AWS_REGION) \
						cloudformation describe-stack-resources \
						--stack-name $(bootstrap-stack-name) \
						| jq -r '.StackResources | map(select(.LogicalResourceId == "LocalBuildArtifactS3Bucket")) | .[0] | .PhysicalResourceId') \
		--s3-prefix $(USER)/cloudformation-package/$(application-name) \
		--output-template-file $@ \
		--template-file $<

.PHONY: deploy-pipeline pipeline

deploy-pipeline: $(pkgd-pipeline-template) $(pkgd-pipeline-config)
	aws --region $(AWS_REGION) \
		cloudformation deploy \
		--capabilities CAPABILITY_IAM \
		--no-fail-on-empty-changeset \
		--parameter-overrides $(shell jq -r ".Parameters | to_entries | map(\"\(.key)=\(.value | tostring)\") | join(\" \")" \
								  $(pkgd-pipeline-config)) \
		--s3-bucket $(shell aws --region $(AWS_REGION) \
						cloudformation describe-stack-resources \
						--stack-name $(bootstrap-stack-name) \
						| jq -r '.StackResources | map(select(.LogicalResourceId == "LocalBuildArtifactS3Bucket")) | .[0] | .PhysicalResourceId') \
		--s3-prefix $(USER)/cloudformation-deploy/$(application-name) \
		--stack-name $(application-name)-pipeline \
		--template-file $<

pipeline: deploy-pipeline

# Lambda build

.PHONY: build test

test:
	boot --no-colors -- test

build:
	boot --no-colors -- build

$(target-dir)/$(application-name).zip: test build
	rm -rf target/node_modules/aws-sdk
	cd target \
		&& zip --quiet --recurse-paths $(application-name).zip * \
		&& cd -

.PHONY: zip
zip: $(target-dir)/$(application-name).zip

# Clone for new application

.PHONY: clone
clone: very-clean
	./clone -a $(application-name) -d $(clone-dir) -n $(application-namespace)
