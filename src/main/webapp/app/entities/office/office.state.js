(function() {
    'use strict';

    angular
        .module('leafNgApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('office', {
            parent: 'entity',
            url: '/office',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'leafNgApp.office.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/office/offices.html',
                    controller: 'OfficeController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('office');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('office-detail', {
            parent: 'entity',
            url: '/office/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'leafNgApp.office.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/office/office-detail.html',
                    controller: 'OfficeDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('office');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Office', function($stateParams, Office) {
                    return Office.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('office.new', {
            parent: 'office',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/office/office-dialog.html',
                    controller: 'OfficeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                location: null,
                                type: null,
                                parentOffice: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('office', null, { reload: true });
                }, function() {
                    $state.go('office');
                });
            }]
        })
        .state('office.edit', {
            parent: 'office',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/office/office-dialog.html',
                    controller: 'OfficeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Office', function(Office) {
                            return Office.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('office', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('office.delete', {
            parent: 'office',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/office/office-delete-dialog.html',
                    controller: 'OfficeDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Office', function(Office) {
                            return Office.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('office', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
