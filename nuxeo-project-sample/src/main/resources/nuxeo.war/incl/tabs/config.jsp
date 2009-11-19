
<div xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:ui="http://java.sun.com/jsf/facelets"
	xmlns:nxu="http://nuxeo.org/nxweb/util"
	xmlns:nxd="http://nuxeo.org/nxweb/document"
	xmlns:a4j="https://ajax4jsf.dev.java.net/ajax">
	
<link rel="stylesheet" href="../css/timesheet.css" type="text/css"/>

<h:form>
	
	<div id="div1">
	
    <h:outputLabel value="#{translation['ws.title']}"/>
    
    <h:dataTable value="#{configurationBean.remoteWSs}" var="ws" 
    	border="1" styleClass="table1"
    	rowClasses="table1rowEven table1rowOdd">
    
    	<h:column>
    		<f:facet name="header">
    			<h:outputText value="#{translation['ws.name']}"/>
    		</f:facet>
    		<h:outputText value="#{ws.name}"/>
    	</h:column>
    
    	<h:column>
    		<f:facet name="header">
    			<h:outputText value="#{translation['ws.endpoint']}"/>
    		</f:facet>
    		<h:outputText value="#{ws.endpoint}"/>
    	</h:column>
    	
    	<h:column>
    		<f:facet name="header">
    			<h:outputText value="#{translation['ws.description']}"/>
    		</f:facet>
    		<h:outputText value="#{ws.description}"/>
    	</h:column>
    	
    	<h:column>
			<h:commandButton value="#{translation['ws.delete']}"
				 action="#{configurationBean.deleteWS}">
				<f:setPropertyActionListener 
				target="#{configurationBean.name}" value="#{ws.name}"/>
				<f:setPropertyActionListener 
				target="#{configurationBean.endpoint}" value="#{ws.endpoint}"/>
			</h:commandButton>
    	</h:column>

    	<h:column>
    		<f:facet name="header">
    			<h:outputText value="edycja"/>
    		</f:facet>
    		
    		<h:commandButton value="edytuj" id="button312321">
    			<f:setPropertyActionListener value="#{ws.name}" target="#{configurationBean.name}"/>
    			<a4j:support reRender="langTable2" event="onclick" 
    			ajaxSingle="true"/>
    		</h:commandButton>
    		
    	</h:column>
    		
    </h:dataTable>
    
</div>
</h:form>

<h:form>
<div id="div2">

		<h:dataTable id="langTable2" border="1" styleClass="table1"
    	rowClasses="table1rowEven table1rowOdd" 
		value="#{configurationBean.selectedWS.supportedTranslation}" var="pair">
		
			<h:column>
				<h:outputText value="#{pair.from}"/>
				<f:facet name="header">
					<h:outputText value="column1"/>
				</f:facet>
			</h:column>
			
			<h:column>
				<h:outputText value="#{pair.to}"/>
				<f:facet name="header">
					<h:outputText value="column2"/>
				</f:facet>
			</h:column>
			
			<h:column>
				<h:commandButton value="delete" 
					action="#{configurationBean.deletePair}">
					<f:setPropertyActionListener
					target="#{configurationBean.toDeletePairLangFrom}" 
					value="#{pair.from}"/>
					<f:setPropertyActionListener
					target="#{configurationBean.toDeletePairLangTo}" 
					value="#{pair.to}"/>
				</h:commandButton>
				<f:facet name="header">
					<h:outputText value="column3"/>
				</f:facet>
			</h:column>
			
	</h:dataTable>


		<h:panelGrid columns="4">
			<h:selectOneMenu value="#{configurationBean.newLangFrom}">
				<f:selectItems value="#{configurationBean.iso3Codes}"/>
			</h:selectOneMenu>
			
			<h:selectOneMenu value="#{configurationBean.newLangTo}">
				<f:selectItems value="#{configurationBean.iso3Codes}"/>
			</h:selectOneMenu>
			<h:commandButton id="addButton1" value="dodaj" action="#{configurationBean.addPair}"/>
			<h:message for="addButton1"/>
		</h:panelGrid>

</div>
</h:form>

<h:form>
<div id="div3">
		
		<h:outputLabel value="#{translation['ws.add.new']}"/>
	
		<h:panelGrid columns="3" styleClass="dataInput"
	      columnClasses="labelColumn, fieldColumn, fieldColumn">
	    
		   	<h:outputLabel value="#{translation['ws.add.name']}" />
		   	<h:inputText id="name" value="#{configurationBean.name}" required="true"/>
			<h:message for="name" styleClass="errorMessage" />
				
	      	<h:outputLabel value="#{translation['ws.add.endpoint']}" />
	      	<h:inputText id="endpoint" value="#{configurationBean.endpoint}" required="true"/>
			<h:message for="endpoint" styleClass="errorMessage" />
	
	      	<h:outputLabel value="#{translation['ws.add.description']}" />
	      	<h:inputText id="description" value="#{configurationBean.description}"/>
			<h:message for="description" styleClass="errorMessage" />
			
		</h:panelGrid>
	
		<h:commandButton 
			value="#{translation['ws.add.register']}"
			action="#{configurationBean.addNewWS}"
			id="registerButton"
			/>
		
		<h:message style="color: red" styleClass="errorMessage" for="registerButton"/>
	</div>
</h:form>
</div>