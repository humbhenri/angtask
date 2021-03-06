(function() {
    'use strict';

    angular
        .module('angtaskApp')
        .controller('TodoDeleteController',TodoDeleteController);

    TodoDeleteController.$inject = ['$scope', '$uibModalInstance', 'entity', 'Todo'];

    function TodoDeleteController($scope, $uibModalInstance, entity, Todo) {
        var vm = this;

        vm.todo = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Todo.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                    $scope.$emit('angtaskApp:todoDelete', null);
                });
        }
    }
})();
