var app = angular.module("TradeInjectorApp.controllers", [
		'angularModalService', 'ngAnimate' ]);


app.factory('UserService', function($http, $q){
	
	var UserService={
			user:[],
			loginType:[],
			getUser: getUser
	};
	
	return UserService;
	
	function getUser(){
		
		 return $http.get("/user").success(function(data) { 
				
				console.log(data.userAuthentication.details.name);
				
				 UserService.user=data.userAuthentication.details.name;
				 
					 if (data.userAuthentication.details.name == null) 
					 { 
						 // must be github
						 UserService.user= data.userAuthentication.details.login;
						 UserService.loginType="github";
					 }else{
						 UserService.loginType="facebook";
					 }
					 console.log("before returning this is the user "+UserService.user+" "+UserService.loginType);
				 });
	}
				
			
	
	
	
});

app.controller("TradeInjectCtrl", function($scope, $http, $location,
		TradeInjectorService, ModalService, UserService) {

	$scope.tradeAcks = [];
	// $scope.tradeInjectorMessage = [];
	$scope.labels = [];
	$scope.data = [];
	$scope.labels_instrument = [];
	$scope.instrumentCount = [];
	$scope.clientCount = [];
	$scope.showGeneration = false;
	$scope.totalMsgCount = [ 0 ];
	$scope.tab = 1;
	$scope.user = [];
	$scope.authenticated = false;
	$scope.loginType = [];

	$scope.setTab = function(newTab) {
		$scope.tab = newTab;
	}

	$scope.isSet = function(tabNum) {
		return $scope.tab === tabNum;
	}

	// login
	
	// UserService.getUserService().success(function(result){
		// $scope.user=result;
	// });
	  UserService.getUser().success(function(user){
		  $scope.user=UserService.user;
		  $scope.authenticated = true;
		  $scope.loginType=UserService.loginType;
			  console.log("+++login type +++"+$scope.loginType+" "+$scope.user);
	  }).error(function(){
		  $scope.user="N/A";
		  $scope.authenticated = false;
	  });
	
		 
	// logout
	$scope.logout = function() {
		$http.post('/logout', {}).success(function() {
			$scope.authenticated = false;
			$location.path("/");
		}).error(function(data) {
			console.log("Logout failed")
			$scope.authenticated = false;
		});
	};

	$scope.datasetOverride = {
		fill : false
	};

	$scope.injectTrades = function() {
		$scope.tradeAcks = [];
		$scope.labels = [];
		$scope.data = [];
		$scope.labels_instrument = [];
		$scope.instrumentCount = [];
		$scope.clientCount = [];
		$scope.totalMsgCount = [ 0 ];
		console.log('Before sending ' + $scope.tradeInjectorMessage);
		// set the user id
		$scope.tradeInjectorMessage.userId = $scope.user
		TradeInjectorService.send($scope.tradeInjectorMessage);
	};

		
	

}

);


app
		.controller(
				'ModalTableController',
				[
						'$scope',
						'$element',
						'injectId',
						'close',
						'TradeInjectorService',
						'filterFilter',
						function($scope, $element, injectId, close,
								TradeInjectorService, filterFilter) {

							$scope.injectId = injectId;
							$scope.tradeAcks = [];
							$scope.labels = [];
							$scope.data = [];
							$scope.labels_instrument = [];
							$scope.instrumentCount = [];
							$scope.clientCount = [];
							$scope.totalMsgCount = [ 0 ];

							$scope.options = {
								responsive : true,
								responsiveAnimationDuration : 10,
								title : {
									display : false,
									text : 'Trade Count by Client '
								}
							};

							$scope.options_instrument = {
								responsive : true,
								responsiveAnimationDuration : 1000,
								title : {
									display : false,
									text : 'Trade Count by Instrument'
								}
							};

							TradeInjectorService
									.receive()
									.then(

											null,
											null,
											function(data) {

												var filteredData = filterFilter(
														data,
														{
															injectorProfileId : injectId
														})
												console
														.log("data received "
																+ data[0].injectorProfileId);
												console.log("injector profile id "
														+ injectId);

												console.log("filtered data "
														+ filteredData[0]);

												if (injectId === filteredData[0].injectorProfileId) {
													console
															.log("Yes we have got the right inject id");
													

													filteredData[0].parties
															.forEach(changePartyData);
													
													function changePartyData(
															element, index,
															array) {

														// first check if the
														// party exist
														var clientNameIndex = $scope.labels
																.indexOf(element.id);

														if (clientNameIndex == -1) {

															// do the push
															$scope.clientCount
																	.push(element.currentTradeCount);
															$scope.labels
																	.push(element.id);

														} else {

															// splice only if
															// the value has
															// changed or dont
															// change the array
															if (element.previousTradeCount != element.currentTradeCount) {
																$scope.labels
																		.splice(
																				clientNameIndex,
																				1,
																				element.id);
																$scope.clientCount
																		.splice(
																				clientNameIndex,
																				1,
																				element.currentTradeCount);
															}

														}

													}
													;

													// do the same for
													// instruments

													

													filteredData[0].instruments
															.forEach(changeInstrumentData);

													function changeInstrumentData(
															instrument, index,
															callback) {
														var instrumentIdIndex = $scope.labels_instrument
																.indexOf(instrument.id);
														if (instrumentIdIndex == -1) {
															// push
															$scope.instrumentCount
																	.push(instrument.currentTradeCount);
															$scope.labels_instrument
																	.push(instrument.id);
														} else {
															// splice
															$scope.instrumentCount
																	.splice(
																			instrumentIdIndex,
																			1,
																			instrument.currentTradeCount);
															$scope.labels_instrument
																	.splice(
																			instrumentIdIndex,
																			1,
																			instrument.id)
														}
													}
													;
												}

											});

							$scope.close = function(result) {
								close(result, 1500);
							}
						} ]);
app.controller('ModalCreateNewController', [
		'$scope',
		'$element',
		'$http',
		'UserService',
		'injectId',
		'close',		
		'filterFilter',
		'isEdit',
		function($scope, $element, $http, UserService, injectId, close, filterFilter, isEdit) {

			$scope.showStatus = [];
			$scope.user=[];
			var profileUser=[];
			$scope.isEdit=[];
			// $scope.tradeInjectorProfile={};
			$scope.isEdit = isEdit;
			UserService.getUser().success(function(user){
				profileUser=UserService.user;
			  }).error(function(){
				  profileUser="N/A";
			  });
			
			// console.log("user in create profile is "+profileUser);
			
			// get the profile for the following profile id
			if(injectId !=null){
				var data = $.param({
					id : injectId
				});

				var config = {
					headers : {
						'Content-Type' : 'application/x-www-form-urlencoded;charset=utf-8;'
					}
				}

				$http
						.post('/getProfile', data,
								config)
						.success(
								function(data, status, headers, config) {
									$scope.tradeInjectorProfile = data;
									$scope.showStatus = "Retrieved data for the following id "+$scope.tradeInjectorProfile.id;
									
								}).error(
								function(data, status, header, config) {
									$scope.showStatus = "Data: "
											+ data + "<hr />status: "
											+ status
											+ "<hr />headers: "
											+ header + "<hr />config: "
											+ config;
								});

				
			}
			

			$scope.addInstruments = function() {
				alert('we are in add Instruments');
			};

			$scope.save = function(isValid) {

				// check to make sure the form is completely
				// valid
				if (isValid) {
					$scope.tradeInjectorProfile.userId=profileUser;
					$scope.showStatus = [];
					var data = $scope.tradeInjectorProfile;
					console.log('Data before post '
							+ $scope.tradeInjectorProfile);

					var config = {
						headers : {
							'Content-Type' : 'application/json;'
						}
					}

					$http.post('/saveTradeInjectProfile', data, config)
							.success(
									function(data, status, headers, config) {
										$scope.tradeInjectorProfile = data;
										$scope.showStatus = "Success! Created with following id  "+$scope.tradeInjectorProfile.id;
										console.log('Data received after save '
												+ data);

									}).error(
									function(data, status, header, config) {
										$scope.showStatus = "Data: " + data
												+ "<hr />status: " + status
												+ "<hr />headers: " + header
												+ "<hr />config: " + config;
									});

				}

			};
			
			

			$scope.close = function(result) {
				close(result, 500);
			};
		} ]);
app
		.controller(
				"TradeInjectProfileTableDisplay",
				function($scope, $http, $location, TradeInjectorService,
						ModalService) {

					$scope.tradeInjectProfiles = [];
					$scope.showStatus = [];

					$http.get("/getAllInjectProfiles").success(function(data) {

						$scope.tradeInjectProfiles = data;
					}).error(function(data) {
						$scope.showStatus = data;
					});

					// refresh the entire table
					$scope.refreshAll = function() {

						$http.get("/getAllInjectProfiles").success(
								function(data) {

									$scope.tradeInjectProfiles = data;
								}).error(function(data) {
							$scope.showStatus = data;
						});

					};

					// stop the run
					$scope.stop = function(profileId) {
						var data = $.param({
							id : profileId
						});

						var config = {
							headers : {
								'Content-Type' : 'application/x-www-form-urlencoded;charset=utf-8;'
							}
						}

						$http
								.post('/tradeMessageStopForProfile', data,
										config)
								.success(
										function(data, status, headers, config) {
											$scope.PostDataResponse = data;
											$scope.showGeneration = false;
										}).error(
										function(data, status, header, config) {
											$scope.ResponseDetails = "Data: "
													+ data + "<hr />status: "
													+ status
													+ "<hr />headers: "
													+ header + "<hr />config: "
													+ config;
										});

						// $http.post('/tradeMessageStop');

					}

					// repeat the run
					$scope.repeat = function(profileId) {
						var data = $.param({
							id : profileId
						});

						var config = {
							headers : {
								'Content-Type' : 'application/x-www-form-urlencoded;charset=utf-8;'
							}
						}

						$http.post('/tradeMessageRepeatForProfile', data,
								config).success(
								function(data, status, headers, config) {
									$scope.PostDataResponse = data;
									$scope.showGeneration = false;
								}).error(
								function(data, status, header, config) {
									$scope.ResponseDetails = "Data: " + data
											+ "<hr />status: " + status
											+ "<hr />headers: " + header
											+ "<hr />config: " + config;
								});

					}

					// play button
					$scope.play = function(profileId) {
						var data = $.param({
							id : profileId
						});

						var config = {
							headers : {
								'Content-Type' : 'application/x-www-form-urlencoded;charset=utf-8;'
							}
						}

						$http
								.post('/tradeMessagePlayForProfile', data,
										config)
								.success(
										function(data, status, headers, config) {
											$scope.PostDataResponse = data;
											$scope.showGeneration = false;
										}).error(
										function(data, status, header, config) {
											$scope.ResponseDetails = "Data: "
													+ data + "<hr />status: "
													+ status
													+ "<hr />headers: "
													+ header + "<hr />config: "
													+ config;
										});

					}
					
					// delete the profile
					$scope.delete=function(profileId){
						
						var data = $.param({
							id : profileId
						});

						var config = {
							headers : {
								'Content-Type' : 'application/x-www-form-urlencoded;charset=utf-8;'
							}
						}

						$http
								.post('/deleteProfile', data,
										config)
								.success(
										function(data, status, headers, config) {
											$scope.PostDataResponse = data;
											$scope.showGeneration = false;
										}).error(
										function(data, status, header, config) {
											$scope.ResponseDetails = "Data: "
													+ data + "<hr />status: "
													+ status
													+ "<hr />headers: "
													+ header + "<hr />config: "
													+ config;
										});


						
					}
					
					// Receives the trade inject messages
					TradeInjectorService.receiveTradeInjectMessage().then(

					null, null, function(data) {
						$scope.tradeInjectProfiles = data;
					});
					
					// show the angular window
					$scope.showCreateProfile = function(profileId) {

						ModalService.showModal({
							templateUrl : '/createNewProfile.html',
							controller : "ModalCreateNewController",
							inputs : {
								injectId : profileId,
								isEdit : "Y"
							}

						}).then(function(modal) {
							modal.element.modal();
							modal.close.then(function(result) {
								$scope.message = "You said " + result;
							});
						});

					};
					
					// view profile only
					$scope.viewProfile = function(injectid) {

						ModalService.showModal({
							templateUrl : '/createNewProfile.html',
							controller : "ModalCreateNewController",
							inputs : {
								injectId : injectid,
								isEdit : "N"
							}

						}).then(function(modal) {
							modal.element.modal();
							modal.close.then(function(result) {
								$scope.message = "You said " + result;
							});
						});

					};

					
					$scope.showTable = function(injectid) {

						ModalService.showModal({
							templateUrl : '/showTableData.html',
							controller : "ModalTableController",
							inputs : {
								injectId : injectid
							}

						}).then(function(modal) {
							modal.element.modal();
							modal.close.then(function(result) {
								$scope.message = "You said " + result;
							});
						});

					};




				});
app.controller("ReportStaticController", function($scope, $http, $location, TradeInjectorService,
		ModalService, filterFilter) {
	
	$scope.tab = 1;
	$scope.pageActive = "A";
	
	$scope.allInstrumentsData = [];
	$scope.allTradeData=[];
	$scope.allPositionData=[];
	$scope.tradeCount=[];
	$scope.showtableView=false;
	$scope.tableIndex=0;
	// GET ALL INSTRUMENTS
	// $http.get("/getAllInstruments").success(function(data) {

		// $scope.allInstrumentsData = data;
	// }).error(function(data) {
		// $scope.showStatus = data;
	// });
	
	$scope.setTab = function(newTab) {
		$scope.tab = newTab;
	}

	$scope.isTab = function(tabNum) {
		return $scope.tab === tabNum;
	}
	
	$scope.isPageActive = function(page) {
		return $scope.pageActive === page;
	}
	
	$scope.clickOnPage=function(pageMarker){
		
		$scope.pageActive = pageMarker;
		
		var data = $.param({
			pageMarker : pageMarker
		});

		var config = {
			headers : {
				'Content-Type' : 'application/x-www-form-urlencoded;charset=utf-8;'
			}
		}

		$http
				.post('/getAllInstruments', data,
						config)
				.success(
						function(data, status, headers, config) {
							$scope.allInstrumentsData = data;
							$scope.showGeneration = false;
						}).error(
						function(data, status, header, config) {
							$scope.ResponseDetails = "Data: "
									+ data + "<hr />status: "
									+ status
									+ "<hr />headers: "
									+ header + "<hr />config: "
									+ config;
						});


		
	}
	
	$scope.refreshTrades = function(){
		
		// get the trade data
		$http({
			  method: 'GET',
			  url: 'http://localhost:8094/tradequeryservice/getAllTrades'
			}).then(function successCallback(response) {
			    // this callback will be called asynchronously
			    // when the response is available
				$scope.allTradeData = response.data;
				
			  }, function errorCallback(response) {
			    // called asynchronously if an error occurs
			    // or server returns response with an error status.
				  $scope.ResponseDetails = "Response: "+ response.data + "<hr />";
			  });
		
	}
	
	$scope.showtable = function(accountid, instrumentid,  rowIndex){
		$scope.showtableView=!$scope.showtableView;
		$scope.tableIndex = rowIndex;
		
		if($scope.showtableView===true){
			$http({
				  method: 'GET',
				  url: 'http://localhost:8094/tradequeryservice/getTradesForPositionAccountAndInstrument/'+accountid+'/'+instrumentid
				}).then(function successCallback(response) {
				    // this callback will be called asynchronously
				    // when the response is available
					console.log("response trade data is "+response);
					$scope.allTradeData = response.data;
					
				  }, function errorCallback(response) {
				    // called asynchronously if an error occurs
				    // or server returns response with an error status.
					  $scope.ResponseDetails = "Response: "+ response.data + "<hr />";
				  });
			
		}
		
		
	}
	
	
	
	$scope.refreshPositions=function(){
		
		// get all the positions
		$http({
			  method: 'GET',
			  url: 'http://localhost:8093/positionqueryservice/getAllPositionAccounts'
			}).then(function successCallback(response) {
			    // this callback will be called asynchronously
			    // when the response is available
				$scope.allPositionData = response.data;
				
			  }, function errorCallback(response) {
			    // called asynchronously if an error occurs
			    // or server returns response with an error status.
				  $scope.ResponseDetails = "Response: "+ response.data + "<hr />";
			  });
		
	}
	
	

	
	
	TradeInjectorService
	.receive()
	.then(

			null,
			null,
			function(data) {

				
					// run through the instruments and add the price data to it

// var filteredData = $scope.allInstrumentsData.filter(function (data) {
// return (data.instruments[id=="A"]);
// });
//
//					
// filteredData[0].instruments
// .forEach(changeInstrumentData);
//					
//					
//
// function changeInstrumentData(
// instrument, index,
// callback) {
//						
//													
// console.log("the instrument id is "+instrument.id);
// if(instrument.id===$scope.allInstrumentsData[index].id){
// $scope.allInstrumentsData[index].price = instrument.price;
// $scope.allInstrumentsData[index].prevPrice = instrument.prevPrice;
//							
// //console.log("full instrument data is
// "+$scope.allInstrumentsData[index].price);
// $scope.allInstrumentsData[index].push;
// }
//						
//											 
//						
// };
				
				// set the trade count
				$scope.tradeCount = data[0].tradeCount;
				// console.log("Trade count is "+data[0].tradeCount);
				
				//also refresh the positions
				$http({
					  method: 'GET',
					  url: 'http://localhost:8093/positionqueryservice/getAllPositionAccounts'
					}).then(function successCallback(response) {
					    // this callback will be called asynchronously
					    // when the response is available
						$scope.allPositionData = response.data;
						
					  }, function errorCallback(response) {
					    // called asynchronously if an error occurs
					    // or server returns response with an error status.
						  $scope.ResponseDetails = "Response: "+ response.data + "<hr />";
					  });
				

			});



		
});



