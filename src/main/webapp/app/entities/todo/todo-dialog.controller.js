(function() {
	'use strict';

	angular.module('angtaskApp').controller('TodoDialogController',
			TodoDialogController);

	TodoDialogController.$inject = [ '$timeout', '$scope', '$stateParams',
			'$uibModalInstance', 'entity', 'Todo', 'User', 'Principal' ];

	function TodoDialogController($timeout, $scope, $stateParams,
			$uibModalInstance, entity, Todo, User, Principal) {
		var vm = this;

		vm.todo = entity;
		vm.clear = clear;
		vm.datePickerOpenStatus = {};
		vm.openCalendar = openCalendar;
		vm.save = save;
		vm.users = User.query();
		vm.currentUser = null;

		(function() {
			Principal.identity().then(function(account) {
				User.get({
					login : account.login
				}).$promise.then(function(user) {
					vm.currentUser = user;
				});
			});
		})();

		$timeout(function() {
			angular.element('.form-group:eq(1)>input').focus();
		});

		function clear() {
			$uibModalInstance.dismiss('cancel');
		}

		function save() {
			vm.isSaving = true;
			if (vm.todo.id !== null) {
				Todo.update(vm.todo, onSaveSuccess, onSaveError);
			} else {
				vm.todo.user = vm.currentUser;
				Todo.save(vm.todo, onSaveSuccess, onSaveError);
			}
		}

		function onSaveSuccess(result) {
			$scope.$emit('angtaskApp:todoUpdate', result);
			$uibModalInstance.close(result);
			vm.isSaving = false;
		}

		function onSaveError() {
			vm.isSaving = false;
		}

		vm.datePickerOpenStatus.created = false;

		function openCalendar(date) {
			vm.datePickerOpenStatus[date] = true;
		}
	}
})();
