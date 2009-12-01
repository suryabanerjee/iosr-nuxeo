<div xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:ui="http://java.sun.com/jsf/facelets"
	xmlns:nxu="http://nuxeo.org/nxweb/util"
	xmlns:nxd="http://nuxeo.org/nxweb/document"
	xmlns:a4j="https://ajax4jsf.dev.java.net/ajax">
	
<h:form>
	
	<div id="div4" style="	width: 40%;
		text-align: center;
		background: #CCFF33;
		margin: 10px;
		position: relative;">
	<h:outputText value="#{translation['translate.filesList']}"/>
	<h:dataTable border="1" value="#{editionBean.filesSelectionBean.files}" var="file"
	styleClass="table1"
    	rowClasses="table1rowEven table1rowOdd">
		<h:column id="column1">
			<a4j:region renderRegionOnly="false">
					<h:selectBooleanCheckbox value="#{file.selected}">
						<a4j:support event="onclick" reRender="output3"
						 ajaxSingle="true"/>
					</h:selectBooleanCheckbox>
			</a4j:region>
			
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

	<div id="div5" style="background: #FFFF99;
		margin: 10px;
		position: relative;
		width: 350px;
		display: block;
		padding: 10px;">

		<h:outputText value="#{translation['translate.chooseOptions']}"/>
		<h:panelGrid border="0" columns="2">
		
			<h:outputLabel value="#{translation['translate.service']}"/>
		
			<a4j:region renderRegionOnly="false">
				<h:selectOneMenu value="#{editionBean.wsName}">
					<f:selectItems value="#{editionBean.availableServices}"/>
					<a4j:support event="onselect" reRender="langFrom, langTo, description1"
							 ajaxSingle="true"/>
				</h:selectOneMenu>
			</a4j:region>
		
			<h:outputLabel value="#{translation['translate.from']}"/>
	
			<a4j:region id="langFrom" renderRegionOnly="false">	
				<h:selectOneMenu value="#{editionBean.langFrom}" style="width: 50px">
					<f:selectItems value="#{editionBean.langsFrom}"/>
					<a4j:support event="onselect" reRender="langTo"
							 ajaxSingle="true"/>
				</h:selectOneMenu>
			</a4j:region>
		
			<h:outputLabel value="#{translation['translate.to']}"/>
			
			<a4j:region id="langTo" renderRegionOnly="false">	
			
				<h:selectOneMenu value="#{editionBean.langTo}" style="width: 50px">
					<f:selectItems value="#{editionBean.langsTo}"/>
				</h:selectOneMenu>
			
			</a4j:region>
				
			<h:inputHidden/>
				
			<h:commandButton value="#{translation['submit.translation']}" 
			actionListener="#{editionBean.validationListener}"
			action="#{editionBean.buildTranslationRequest}"/>
	
			</h:panelGrid>

		<div id="div6" style="	background: #FFFFFF;
			margin: 10px;
			width: 250px;
			heigth: 50px;
			border-style: dotted;
			border-width: 1px;">
		<h:outputLabel value="#{translation['translate.service.description']}"/>
		
		<a4j:region id="description1" renderRegionOnly="false">	
			<h:outputText value="#{editionBean.remoteWSDescription.description}"/>
		</a4j:region>
		</div>
		
	</div>
	
	<div id="div7" style="	background: #FFFF99;
			margin: 10px;
			position: relative;
			width: 350px;
			display: block;">
		<h:messages layout="table"/>
		<h:outputText value="#{editionBean.report}" rendered="#{editionBean.hasReport}"/>
	</div>

</h:form>
</div>