(function() {
    'use strict';

    angular
        .module('leafNgApp')
        .controller('OfficeDetailController', OfficeDetailController);

    OfficeDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Office'];

    function OfficeDetailController($scope, $rootScope, $stateParams, entity, Office) {
        var vm = this;

        vm.office = entity;

        var unsubscribe = $rootScope.$on('leafNgApp:officeUpdate', function(event, result) {
            vm.office = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
