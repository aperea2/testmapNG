(function() {
    'use strict';

    angular
        .module('leafNgApp')
        .controller('OfficeController', OfficeController);

    OfficeController.$inject = ['$scope', '$state', 'Office', 'OfficeSearch'];

    function OfficeController ($scope, $state, Office, OfficeSearch) {
        var vm = this;
        
        vm.offices = [];
        vm.search = search;

        loadAll();

        function loadAll() {
            Office.query(function(result) {
                vm.offices = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            OfficeSearch.query({query: vm.searchQuery}, function(result) {
                vm.offices = result;
            });
        }    }
})();
