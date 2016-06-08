(function() {
    'use strict';

    angular.module('leafNgApp')
        .controller('HealthModalController', HealthModalController);

    HealthModalController.$inject = ['$uibModalInstance', 'currentHealth', 'baseName', 'subSystemName'];

    function HealthModalController ($uibModalInstance, currentHealth, baseName, subSystemName) {
        var vm = this;

        vm.cancel = cancel;
        vm.currentHealth = currentHealth;
        vm.baseName = baseName;
        vm.subSystemName = subSystemName;

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();
