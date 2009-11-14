<div xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:ui="http://java.sun.com/jsf/facelets"
	xmlns:nxu="http://nuxeo.org/nxweb/util"
	xmlns:nxd="http://nuxeo.org/nxweb/document"
	xmlns:a4j="https://ajax4jsf.dev.java.net/ajax">
	
<link rel="stylesheet" href="./css/timesheet.css" type="text/css"/>

<h:form>
	
	<div id="div4">
	<h:outputText value="#{translation['translate.filesList']}"/>
	<h:dataTable border="1" value="#{editionBean.filesSelectionBean.files}" var="file"
	styleClass="table1"
    	rowClasses="table1rowEven table1rowOdd">
		<h:column id="column1">
				<h:selectBooleanCheckbox value="#{file.selected}">
					<a4j:support event="onclick" reRender="output3"
					 ajaxSingle="true"/>
				</h:selectBooleanCheckbox>
			<f:facet name="header">
				<h:outputText value="#{translation['translate.filesList.select']}"/>
			</f:facet>
		</h:column>
		<h:column id="column2">
			<h:outputText value="#{file.name}"/>
			<f:facet name="header">
				<h:outputText value="#{translation['translate.filesList.fileName']}"></h:outputText>
			</f:facet>
		</h:column>
		<h:column>
			<f:facet name="header">
				<h:outputText value="#{translation['translate.filesList.targetName']}"/>
			</f:facet>
			<h:panelGroup id="output3">
				<h:inputText style="border: 0px" value="#{file.targetName}" rendered="#{file.selected}" required="#{file.selected}"/>
			</h:panelGroup>
		</h:column>
	</h:dataTable>
	</div>

	<div id="div5">
	<h:outputText value="#{translation['translate.chooseOptions']}"/>
	<h:panelGrid border="0" columns="2">
	
		<h:outputLabel value="#{translation['translate.service']}"/>
	
		<h:selectOneMenu value="#{editionBean.wsName}">
			<f:selectItems value="#{editionBean.availableServices}"/>
			<a4j:support event="onchange" reRender="langFrom,description1"
					 bypassUpdates="false" ajaxSingle="true"/>
		</h:selectOneMenu>

		<h:outputLabel value="#{translation['translate.from']}"/>

		<h:selectOneMenu id="langFrom" value="#{editionBean.langFrom}" style="width: 50px">
			<f:selectItems value="#{editionBean.langsFrom}"/>
			<a4j:support event="onchange" reRender="langTo"
					 bypassUpdates="false" ajaxSingle="true"/>
		</h:selectOneMenu>

		<h:outputLabel value="#{translation['translate.to']}"/>
		
		<h:selectOneMenu id="langTo" value="#{editionBean.langTo}" style="width: 50px">
			<f:selectItems value="#{editionBean.langsTo}"/>
		</h:selectOneMenu>
		
		<h:inputHidden/>
			
		<h:commandButton value="#{translation['submit.translation']}" 
		actionListener="#{editionBean.validationListener}"
		action="#{editionBean.buildTranslationRequest}"/>

		</h:panelGrid>
		
		<div id="div6">
		<h:outputLabel value="#{translation['translate.service.description']}"/>
		<h:outputText id="description1" 
		value="#{editionBean.remoteWSDescription.description}"/>
		</div>
		
	</div>
	
	<div id="div7">
		<h:messages layout="table"/>
		<h:outputText value="#{editionBean.report}" rendered="#{editionBean.hasReport}"/>
	</div>

</h:form>
</div>