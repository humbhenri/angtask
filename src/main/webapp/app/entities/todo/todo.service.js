(function() {
    'use strict';
    angular
        .module('angtaskApp')
        .factory('Todo', Todo);

    Todo.$inject = ['$resource', 'DateUtils'];

    function Todo ($resource, DateUtils) {
        var resourceUrl =  'api/todos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.created = DateUtils.convertLocalDateFromServer(data.created);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.created = DateUtils.convertLocalDateToServer(data.created);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.created = DateUtils.convertLocalDateToServer(data.created);
                    return angular.toJson(data);
                }
            }
        });
    }
})();
