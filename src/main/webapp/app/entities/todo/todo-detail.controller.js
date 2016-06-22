(function() {
    'use strict';

    angular
        .module('angtaskApp')
        .controller('TodoDetailController', TodoDetailController);

    TodoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Todo', 'User'];

    function TodoDetailController($scope, $rootScope, $stateParams, entity, Todo, User) {
        var vm = this;

        vm.todo = entity;

        var unsubscribe = $rootScope.$on('angtaskApp:todoUpdate', function(event, result) {
            vm.todo = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
