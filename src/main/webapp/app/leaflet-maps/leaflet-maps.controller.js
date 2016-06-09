(function() {
	'use strict';

	angular.module('leafNgApp').controller('LeafletMapsController',
			LeafletMapsController);

	LeafletMapsController.$inject = [ '$scope', 'leafletData', 'Office' ];

	function LeafletMapsController($scope, leafletData, Office) {

		var vm = this;

		vm.offices = [];
		
		loadAll();

        function loadAll() {
            Office.query(function(result) {
                vm.offices = result;
            });
        }
        
		angular.extend($scope, {
			center : {
				lat: 51.505,
			      lng: -0.09,
			      zoom: 12
			},
			overlays : {
				search : {
					name : 'search',
					type : 'group',
					visible : true,
					layerParams : {
						showOnSelector : false
					}
				}
			}
		});

		leafletData.getLayers().then(function(baselayers) {
			console.log(baselayers.overlays.search);
			angular.extend($scope.controls, {
				search : {
					layer : baselayers.overlays.search
				}
			});
		});

		leafletData
				.getMap()
				.then(
						function(map) {
							map
									.addControl(new L.Control.Search(
											{
												url : 'http://nominatim.openstreetmap.org/search?format=json&q={s}',
												jsonpParam : 'json_callback',
												propertyName : 'display_name',
												propertyLoc : [ 'lat', 'lon' ],
												circleLocation : false,
												markerLocation : false,
												autoType : false,
												autoCollapse : true,
												minLength : 2,
												zoom : 10
											}));
							
							var polygonOffice = L.polygon(vm.offices).addTo(map);
							
							
							
							var marker = L.marker([51.5, -0.09]).addTo(map);
							
						});
	
		
	
	}
})();
