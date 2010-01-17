<div xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:ui="http://java.sun.com/jsf/facelets"
	xmlns:nxu="http://nuxeo.org/nxweb/util"
	xmlns:nxd="http://nuxeo.org/nxweb/document"
	xmlns:nxh="http://nuxeo.org/nxweb/html"
	xmlns:nxdir="http://nuxeo.org/nxdirectory"
	xmlns:a4j="https://ajax4jsf.dev.java.net/ajax"><h:form>

	<a4j:region>
		<div id="div4"
			style="width: 510px; text-align: center; background: #CCFF33; margin: 10px; position: relative;">
		<h:outputText value="#{translation['translate.filesList']}" /> <a4j:outputPanel
			id="output3">
			<h:dataTable border="1"
				value="#{editionBean.filesSelectionBean.files}" var="file"
				styleClass="table1" rowClasses="table1rowEven table1rowOdd">
				<h:column id="column1">
					<h:selectBooleanCheckbox value="#{file.selected}">
						<a4j:support event="onchange" reRender="output3" ajaxSingle="true" />
					</h:selectBooleanCheckbox>

					<f:facet name="header">
						<h:outputText value="#{translation['translate.filesList.select']}"
							style="width: 100px;" />
					</f:facet>
				</h:column>
				<h:column id="column2">
					<h:outputText value="#{file.name}" />
					<f:facet name="header">
						<h:outputText
							value="#{translation['translate.filesList.fileName']}"
							style="width: 100px;">
						</h:outputText>
					</f:facet>
				</h:column>
				<h:column>
					<f:facet name="header">
						<h:outputText
							value="#{translation['translate.filesList.targetName']}"
							style="width: 200px;" />
					</f:facet>
					<h:inputText value="#{file.targetName}" rendered="#{file.selected}"
						required="#{file.selected}" />
				</h:column>

				<h:column>
					<f:facet name="header">
						<h:outputText value="#{translation['display.history']}"
							style="width: 100px;" />
					</f:facet>
					<h:panelGrid columns="2">
						<a4j:commandButton value="#{translation['display']}"
							actionListener="#{editionBean.showHistory}" reRender="history">
							<f:attribute name="file" value="#{file}" />
						</a4j:commandButton>
						<a4j:commandButton value="#{translation['refresh']}"
							actionListener="#{editionBean.refreshHistory}" reRender="history">
						</a4j:commandButton>
					</h:panelGrid>
				</h:column>

			</h:dataTable>

		</a4j:outputPanel></div>
	</a4j:region>


	<a4j:region>
		<div id="div5"
			style="background: #FFFF99; margin: 10px; position: relative; width: 350px; display: block; padding: 10px;">

		<h:outputText value="#{translation['translate.chooseOptions']}" /> <h:panelGrid
			border="0" columns="2">

			<h:outputLabel value="#{translation['translate.service']}" />

			<nxh:selectOneMenu value="#{editionBean.wsName}">
				<f:selectItems value="#{editionBean.availableServices}" />
				<a4j:support event="onchange"
					reRender="detection,langFrom,langTo,description,quality"
					bypassUpdates="false" ajaxSingle="true" />
			</nxh:selectOneMenu>


			<h:outputLabel value="#{translation['translate.detection']}" />

			<a4j:outputPanel id="detection">
				<nxh:selectBooleanCheckbox value="#{editionBean.languageDetection}"
					disabled="#{!editionBean.translationServiceDescription.languageDetection}">
					<a4j:support event="onchange" reRender="langTo,langFrom"
						bypassUpdates="false" ajaxSingle="true" />
				</nxh:selectBooleanCheckbox>
			</a4j:outputPanel>


			<h:outputLabel value="#{translation['translate.from']}" />

			<a4j:outputPanel id="langFrom">
				<nxh:selectOneMenu value="#{editionBean.langFrom}"
					style="width: 150px" disabled="#{editionBean.languageDetection}">
					<f:selectItems value="#{editionBean.langsFrom}" />
					<a4j:support event="onchange" reRender="langTo"
						bypassUpdates="false" ajaxSingle="true" />
				</nxh:selectOneMenu>
			</a4j:outputPanel>

			<h:outputLabel value="#{translation['translate.to']}" />

			<a4j:outputPanel id="langTo">
				<nxh:selectOneMenu value="#{editionBean.langTo}"
					style="width: 150px" disabled="#{editionBean.languageDetection}">
					<f:selectItems value="#{editionBean.langsTo}" />
				</nxh:selectOneMenu>
			</a4j:outputPanel>

			<h:outputLabel value="#{translation['translate.quality']}" />

			<a4j:outputPanel id="quality">
				<nxh:selectOneMenu value="#{editionBean.quality}"
					style="width: 150px">
					<f:selectItems value="#{editionBean.qualities}" />
				</nxh:selectOneMenu>
			</a4j:outputPanel>

			<h:inputHidden />

			<h:commandButton value="#{translation['submit.translation']}"
				actionListener="#{editionBean.validationListener}"
				action="#{editionBean.buildTranslationRequest}" />

		</h:panelGrid>

		<div id="div6"
			style="background: #FFFFFF; margin: 10px; width: 250px; heigth: 50px; border-style: dotted; border-width: 1px;">
		<h:outputLabel value="#{translation['translate.service.description']}" />

		<a4j:outputPanel id="description">
			<h:outputText
				value="#{editionBean.translationServiceDescription.description}" />
		</a4j:outputPanel></div>

		</div>

		<div id="div7"
			style="background: #FFFF99; margin: 10px; position: relative; width: 350px; display: block;">
		<h:messages layout="table" globalOnly="true" /> <h:outputText
			value="#{editionBean.report}" rendered="#{editionBean.hasReport}" /></div>
	</a4j:region>

	<a4j:region>
		<div id="div4"
			style="width: 100%; text-align: center; background: #AA4455; margin: 10px; position: relative;">
		<a4j:outputPanel id="history">
			<h:dataTable border="1" value="#{editionBean.toHistory}" var="order"
				styleClass="table1" rendered="#{! empty editionBean.toHistory}">

				<h:column id="column9">
					<f:facet name="header">
						<h:outputText value="#{translation['history.source.document']}" style="width: 150px;"/>
					</f:facet>
					<h:outputText value="#{order.sourceDocument.name}" />
				</h:column>

				<h:column id="column10">
					<f:facet name="header">
						<h:outputText value="#{translation['history.destination.document']}" style="width: 150px;"/>
					</f:facet>
					<h:outputText value="#{order.destinationDocument.name}" />
				</h:column>

				<h:column id="column12">
					<f:facet name="header">
						<h:outputText value="#{translation['history.source.lang']}"  style="width: 75px;"/>
					</f:facet>
					<h:outputText value="#{order.langPair.fromLang}" />
				</h:column>

				<h:column id="column13">
					<f:facet name="header">
						<h:outputText value="#{translation['history.source.lang']}"  style="width: 75px;"/>
					</f:facet>
					<h:outputText value="#{order.langPair.toLang}" />
				</h:column>

				<h:column id="column14">
					<f:facet name="header">
						<h:outputText value="#{translation['history.order.status']}"  style="width: 125px;"/>
					</f:facet>
					<h:outputText value="#{order.state}">
						<f:converter converterId="stateConverter" />
					</h:outputText>
				</h:column>

				<h:column id="column15">
					<f:facet name="header">
						<h:outputText value="#{translation['history.translation.steps']}" />
					</f:facet>

					<h:panelGrid columns="2">

						<h:outputLabel value="#{translation['history.translation.steps.submit']}:" />
						<h:outputLabel value="#{order.before}">
							<f:convertDateTime dateStyle="full" />
						</h:outputLabel>

						<h:outputLabel value="#{translation['history.translation.steps.conversion']}:" />
						<h:outputLabel value="#{order.conversion}">
							<f:convertDateTime dateStyle="full" />
						</h:outputLabel>

						<h:outputLabel value="#{translation['history.translation.steps.translation']}:" />
						<h:outputLabel value="#{order.processing}">
							<f:convertDateTime dateStyle="full" />
						</h:outputLabel>

						<h:outputLabel value="#{translation['history.translation.steps.reconversion']}:" />
						<h:outputLabel value="#{order.reconversion}">
							<f:convertDateTime dateStyle="full" />
						</h:outputLabel>

						<h:outputLabel value="#{translation['history.translation.steps.finish']}:" />
						<h:outputLabel value="#{order.succeeded}">
							<f:convertDateTime dateStyle="full" />
						</h:outputLabel>

						<h:outputLabel value="#{translation['history.translation.steps.fail']}:" />
						<h:outputLabel value="#{order.failed}" style="font-color: red;">
							<f:convertDateTime dateStyle="full" />
						</h:outputLabel>

					</h:panelGrid>

				</h:column>

				<h:column id="column16">
					<f:facet name="header">
						<h:outputText value="#{translation['history.translation.error.message']}" style="font-color: red;"/>
					</f:facet>
					<h:outputText value="#{order.message}" />
				</h:column>

			</h:dataTable>

		</a4j:outputPanel></div>
	</a4j:region>

</h:form></div>