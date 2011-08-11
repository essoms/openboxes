<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="custom" />
<title>Add request items</title>

</head>
<body>
	<div class="body">
		<g:if test="${flash.message}">
			<div class="message">
				${flash.message}
			</div>
		</g:if>	
	
		<g:hasErrors bean="${command}">
			<div class="errors">
				<g:renderErrors bean="${command}" as="list" />
			</div>
		</g:hasErrors>
				
		<div class="dialog">
			<fieldset>
				<g:render template="../request/summary" model="[requestInstance:command?.request]"/>				
				<g:render template="progressBar" model="['state':'pickRequestItems']"/>		
				<g:form action="fulfillRequest" autocomplete="false">
					<table>
						<tr>
							<td style="padding: 0; margin: 0;">														
								<div>
									<g:if test="${command?.request?.requestItems }">
										<table id="requestItemsTable" border="0">
											<thead>
												<tr class="odd prop">
													<td class="center" colspan="4">
														<img src="${createLinkTo(dir:'images/icons/silk',file:'cart.png')}" alt="requested" style="vertical-align: middle"/>
														Requested
													</td>
													<td class="center" colspan="4" style="border-left: 1px solid lightgrey;">
														<img src="${createLinkTo(dir:'images/icons/silk',file:'lorry.png')}" alt="received" style="vertical-align: middle"/>
														Fulfilled
													</td>
												</tr>
												<tr class="even">
													<td>Description</td>
													<td class="center">Requested</td>										
													<td class="center">Fulfilled</td>										
													<td>Actions</td>
													<%--<td class="center">Remaining</td> --%>	
													<td class="center" style="width: 100px; border-left: 1px solid lightgrey;">Fulfilled</td>										
													<td style="width: 250px">Product</td>										
													<td style="width: 100px">Lot Number</td>
													<td style="width: 100px">Expires</td>
												</tr>
											</thead>									
											<tbody>
												<g:each var="requestItem" in="${command?.request?.requestItems }" status="i">
													
													<tr class="prop ${i%2?'even':'odd' }">
														<td>
															<g:if test="${requestItem.product }">
																<g:link controller="inventoryItem" action="showStockCard" id="${requestItem?.product?.id }">
																	${requestItem?.description }
																</g:link>
															</g:if>
															<g:else>
																${requestItem?.description }
															</g:else>																
														</td>
														<td class="center">															
															${requestItem?.quantity}
														</td>
														<td>
															${command?.quantityFulfilledMap()[requestItem]}														
														</td>
														<td>
															<g:link action="fulfillRequest" event="showPickDialog" params="['requestItem.id':requestItem?.id]">
																<img src="${resource(dir: 'images/icons/silk', file: 'add.png') }"/>
																<warehouse:message code="request.pickItem.label"/>
															</g:link>
														</td>
														<td colspan="4" class="center" style="padding: 0px; border-left: 1px solid lightgrey;">
															<g:set var="pickItems" value="${command?.fulfillmentItems(requestItem) }"/>
															<g:if test="${pickItems }">																
																<table>
																	<g:each var="pickItem" in="${pickItems }" status="j">
																		<tr class="${j%2?'even':'odd'}">
																			<td style="width: 100px" class="center">
																				${pickItem?.quantity }
																			</td>
																			<td style="width: 250px;" >
																				${pickItem?.inventoryItem?.product?.name }
																			</td>
																			<td style="width: 100px;">
																				${pickItem?.inventoryItem?.lotNumber }
																			</td>
																			<td style="width: 100px;">
																				${formatDate(date: pickItem?.inventoryItem?.expirationDate, format: org.pih.warehouse.core.Constants.DEFAULT_DATE_FORMAT) }

																			</td>
																		</tr>
																	</g:each>
																</table>
															</g:if>
														</td>
													</tr>
												</g:each>
											</tbody>
										</table>
									</g:if>
								</div>
							</td>		
						</tr>
						<tr class="prop">
							<td>
								<div class="buttons" style="border-top: 0px solid lightgrey;">
									<g:submitButton name="back" value="Back"></g:submitButton>
									<g:submitButton name="next" value="Next"></g:submitButton>
									<g:link action="fulfillRequest" event="cancel">Cancel</g:link>
								</div>
							</td>
						</tr>
						
					</table>
				</g:form>
			</fieldset>
		</div>
		<%-- 
		<div id="dialog">
			<div id="dialog-form">
				<!-- populated dynamically -->			
			</div>
		</div>
		--%>			
		
		
		<g:if test="${showDialog}">
			<g:render template="pickItem" model="['requestItem':requestItem]"/>
		</g:if>


	</div>	  
	
</body>
</html>