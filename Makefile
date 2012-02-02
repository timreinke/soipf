# Compile less documents.  Adopted from twitter bootstrap's Makefile
SRC = ./src/resources
DST = ./resources/public
APP_LESS = ${SRC}/less/app.less
APP_JS = app.js
BOOTSTRAP_JS = bootstrap-transition.js bootstrap-alert.js bootstrap-button.js  \
	bootstrap-carousel.js bootstrap-collapse.js bootstrap-dropdown.js \
	bootstrap-modal.js bootstrap-tooltip.js bootstrap-popover.js \
	bootstrap-scrollspy.js bootstrap-tab.js bootstrap-typeahead.js

LESS_COMPRESSOR ?= `which lessc`
WATCHR ?= `which watchr`


#
# BUILD SIMPLE BOOTSTRAP DIRECTORY
# lessc & uglifyjs are required
#

bootstrap:
	mkdir -p $(DST)/img
	mkdir -p $(DST)/css
	mkdir -p $(DST)/js

	cp $(SRC)/img/* $(DST)/img/

	lessc $(APP_LESS) > $(DST)/css/app.css
	lessc --compress $(APP_LESS) > $(DST)/css/app.css

	cat $(addprefix $(SRC)/js/bootstrap/, $(BOOTSTRAP_JS)) > $(DST)/js/bootstrap.js
	uglifyjs -nc ${DST}/js/bootstrap.js > $(DST)/js/bootstrap.min.js

	cat $(addprefix $(SRC)/js/, $(APP_JS)) > $(DST)/js/app.js
	uglifyjs -nc $(DST)/js/app.js > $(DST)/js/app.min.js

#
# WATCH LESS FILES
#

watch:
	echo "Watching less files..."; \
	watchr -e "watch('less/.*\.less') { system 'make' }"


.PHONY: dist docs watch gh-pages
