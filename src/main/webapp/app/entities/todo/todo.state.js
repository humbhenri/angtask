(function() {
    'use strict';

    angular
        .module('angtaskApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('todo', {
            parent: 'entity',
            url: '/todo?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Todos'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/todo/todos.html',
                    controller: 'TodoController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }]
            }
        })
        .state('todo-detail', {
            parent: 'entity',
            url: '/todo/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Todo'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/todo/todo-detail.html',
                    controller: 'TodoDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Todo', function($stateParams, Todo) {
                    return Todo.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('todo.new', {
            parent: 'todo',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/todo/todo-dialog.html',
                    controller: 'TodoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                body: null,
                                created: Date.now(),
                                done: false,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('todo', null, { reload: true });
                }, function() {
                    $state.go('todo');
                });
            }]
        })
        .state('todo.edit', {
            parent: 'todo',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/todo/todo-dialog.html',
                    controller: 'TodoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Todo', function(Todo) {
                            return Todo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('todo', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('todo.delete', {
            parent: 'todo',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/todo/todo-delete-dialog.html',
                    controller: 'TodoDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Todo', function(Todo) {
                            return Todo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('todo', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
