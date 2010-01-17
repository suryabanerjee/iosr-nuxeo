
<div xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:ui="http://java.sun.com/jsf/facelets"
	xmlns:nxu="http://nuxeo.org/nxweb/util"
	xmlns:nxd="http://nuxeo.org/nxweb/document"
	xmlns:a4j="https://ajax4jsf.dev.java.net/ajax"><h:form>

	<div id="div1"
		style="padding: 10px; width: 90%; text-align: center; height: 40%; background: #CCFF00; margin: 10px;">

	<h:outputLabel value="#{translation['ws.title']}" /> <h:dataTable
		value="#{configurationBean.translationServices}" var="ws" border="1"
		styleClass="table1" style="width: 100%;
	background: #99FFFF;"
		rowClasses="table1rowEven table1rowOdd">

		<h:column>
			<f:facet name="header">
				<h:outputText value="#{translation['ws.name']}" />
			</f:facet>
			<h:outputText value="#{ws.name}" />
		</h:column>

		<h:column>
			<f:facet name="header">
				<h:outputText value="#{translation['ws.endpoint']}" />
			</f:facet>
			<h:outputText value="#{ws.endpoint}" />
		</h:column>

		<h:column>
			<f:facet name="header">
				<h:outputText value="#{translation['ws.description']}" />
			</f:facet>
			<h:outputText value="#{ws.description}" />
		</h:column>

		<h:column>
			<f:facet name="header">
				<h:outputText value="#{translation['ws.show.details']}" />
			</f:facet>

			<h:commandButton value="#{translation['ws.show.details.short']}">
				<f:setPropertyActionListener target="#{configurationBean.name}"
					value="#{ws.name}" />
				<a4j:support reRender="details" event="onclick" ajaxSingle="true" />
			</h:commandButton>

		</h:column>

		<h:column>
			<f:facet name="header">
				<h:outputText value="#{translation['ws.refresh.configuration']}" />
			</f:facet>

			<h:commandButton value="#{translation['ws.refresh.now']}"
				actionListener="#{configurationBean.refreshWs}">
				<f:attribute name="ws" value="#{ws}" />
			</h:commandButton>

		</h:column>

		<h:column>
			<f:facet name="header">
				<h:outputText value="#{translation['ws.deletion']}" />
			</f:facet>

			<h:commandButton value="#{translation['ws.delete']}"
				actionListener="#{configurationBean.deleteWS}">
				<f:attribute name="czopson" value="#{ws.name}" />
			</h:commandButton>
		</h:column>

	</h:dataTable></div>
</h:form> <h:form>
	<div id="div2"
		style="margin: 10px; padding: 10px; width: 250px; text-align: center; background: #FFCC33; position: relative; float: left;">

	<h:panelGrid columns="2" id="details">

		<h:outputLabel value="#{translation['ws.details.supported.translations']}:" />

		<h:outputLabel rendered="#{empty configurationBean.selectedWS}"
			value="#{translation['ws.details.service.not.chosen']}"/>

		<h:dataTable border="1" styleClass="table1"
			rowClasses="table1rowEven table1rowOdd"
			value="#{configurationBean.selectedWS.supportedLangPairs}" var="pair"
			rendered="#{! empty configurationBean.selectedWS}">

			<h:column>
				<h:outputText value="#{pair.fromLang}" />
				<f:facet name="header">
					<h:outputText value="#{translation['ws.details.source.lang']}" />
				</f:facet>
			</h:column>

			<h:column>
				<h:outputText value="#{pair.toLang}" />
				<f:facet name="header">
					<h:outputText value="#{translation['ws.details.destination.lang']}" />
				</f:facet>
			</h:column>

		</h:dataTable>

		<h:outputLabel value="#{translation['ws.details.language.detection']}:" />

		<h:outputLabel rendered="#{empty configurationBean.selectedWS}"
			value="#{translation['ws.details.service.not.chosen']}"/>

		<h:selectBooleanCheckbox
			value="#{configurationBean.selectedWS.languageDetection}"
			readonly="true" 
			rendered="#{! empty configurationBean.selectedWS}"/>

		<h:outputLabel value="#{translation['ws.details.supported.qualities']}:" />

		<h:outputLabel rendered="#{empty configurationBean.selectedWS}"
			value="#{translation['ws.details.service.not.chosen']}"/>

		<h:dataTable border="1" styleClass="table1"
			rowClasses="table1rowEven table1rowOdd"
			value="#{configurationBean.selectedWS.supportedQualities}"
			var="quality"
			rendered="#{! empty configurationBean.selectedWS}">

			<h:column>
				<h:outputText value="#{quality.value}" />
				<f:facet name="header">
					<h:outputText value="#{translation['ws.details.quality.level']}" />
				</f:facet>
			</h:column>

		</h:dataTable>

		<h:outputLabel value="#{translation['ws.details.supported.document.types']}:" />

		<h:outputLabel rendered="#{empty configurationBean.selectedWS}"
			value="#{translation['ws.details.service.not.chosen']}"/>

		<h:dataTable border="1" styleClass="table1"
			rowClasses="table1rowEven table1rowOdd"
			value="#{configurationBean.selectedWS.supportedDocumentTypes}"
			var="dt"
			rendered="#{! empty configurationBean.selectedWS}">

			<h:column>
				<h:outputText value="#{dt.value}" />
				<f:facet name="header">
					<h:outputText value="#{translation['ws.details.document.type']}" />
				</f:facet>
			</h:column>

		</h:dataTable>
	</h:panelGrid></div>
</h:form> <h:form>
	<div id="div3"
		style="margin: 10px; padding: 10px; width: 40%; text-align: center; background: #CCFFFF; margin-left: 30px; float: left;">

	<h:outputLabel value="#{translation['ws.add.new']}" /> <h:panelGrid
		columns="3" styleClass="dataInput"
		columnClasses="labelColumn, fieldColumn, fieldColumn">

		<h:outputLabel value="#{translation['ws.add.name']}" />
		<h:inputText id="name" value="#{configurationBean.name}"
			required="true" />
		<h:message for="name" styleClass="errorMessage" style="width: 200px;"/>

		<h:outputLabel value="#{translation['ws.add.endpoint']}" />
		<h:inputText id="endpoint" value="#{configurationBean.endpoint}"
			required="true" />
		<h:message for="endpoint" styleClass="errorMessage" style="width: 200px;" />

		<h:outputLabel value="#{translation['ws.add.description']}" />
		<h:inputText id="description" value="#{configurationBean.description}" />
		<h:message for="description" styleClass="errorMessage" />

	</h:panelGrid> <h:commandButton value="#{translation['ws.add.register']}"
		actionListener="#{configurationBean.addNewWS}" id="registerButton" />
	</div>
	<h:message style="font-color: red" styleClass="errorMessage"
		for="registerButton" />
</h:form></div>