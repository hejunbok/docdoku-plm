/*global $,_,define,App*/
define(function () {

    /**
     * Jquery Query builder doc : http://mistic100.github.io/jQuery-QueryBuilder
     * Selectize Events doc : https://github.com/brianreavis/selectize.js/blob/master/docs/events.md
     */


    $.fn.queryBuilder.defaults({ lang: {
        'add_rule': '',
        'add_group': '',
        'delete_rule': '',
        'delete_group': '',
        'conditions': {
            'AND': App.config.i18n.QUERY_BUILDER_CONDITION_AND,
            'OR': App.config.i18n.QUERY_BUILDER_CONDITION_OR
        },
        'operators': {
            'equal': App.config.i18n.QUERY_BUILDER_OPERATORS_EQUALS,
            'not_equal': App.config.i18n.QUERY_BUILDER_OPERATORS_NOT_EQUALS,
            'in': App.config.i18n.QUERY_BUILDER_OPERATORS_IN,
            'not_in': App.config.i18n.QUERY_BUILDER_OPERATORS_NOT_IN,
            'less': App.config.i18n.QUERY_BUILDER_OPERATORS_LESS,
            'less_or_equal': App.config.i18n.QUERY_BUILDER_OPERATORS_LESS_OR_EQUAL,
            'greater': App.config.i18n.QUERY_BUILDER_OPERATORS_GREATER,
            'greater_or_equal': App.config.i18n.QUERY_BUILDER_OPERATORS_GREATER_OR_EQUAL,
            'between': App.config.i18n.QUERY_BUILDER_OPERATORS_BETWEEN,
            'begins_with': App.config.i18n.QUERY_BUILDER_OPERATORS_BEGIN_WITH,
            'not_begins_with': App.config.i18n.QUERY_BUILDER_OPERATORS_NOT_BEGIN_WITH,
            'contains': App.config.i18n.QUERY_BUILDER_OPERATORS_CONTAINS,
            'not_contains': App.config.i18n.QUERY_BUILDER_OPERATORS_NOT_CONTAINS,
            'ends_with': App.config.i18n.QUERY_BUILDER_OPERATORS_ENDS_WITH,
            'not_ends_with': App.config.i18n.QUERY_BUILDER_OPERATORS_NOT_ENDS_WITH,
            'is_empty': App.config.i18n.QUERY_BUILDER_OPERATORS_IS_EMPTY,
            'is_not_empty': App.config.i18n.QUERY_BUILDER_OPERATORS_IS_NOT_EMPTY,
            'is_null': App.config.i18n.QUERY_BUILDER_OPERATORS_IS_NULL,
            'is_not_null': App.config.i18n.QUERY_BUILDER_OPERATORS_IS_NOT_NULL
        },
        'errors': {
            'no_filter': App.config.i18n.QUERY_BUILDER_ERRORS_NO_FILTER,
            'empty_group': App.config.i18n.QUERY_BUILDER_ERRORS_EMPTY_GROUP,
            'radio_empty': App.config.i18n.QUERY_BUILDER_ERRORS_RADIO_EMPTY,
            'checkbox_empty': App.config.i18n.QUERY_BUILDER_ERRORS_CHECKBOX_EMPTY,
            'select_empty': App.config.i18n.QUERY_BUILDER_ERRORS_SELECT_EMPTY,
            'string_empty': App.config.i18n.QUERY_BUILDER_ERRORS_STRING_EMPTY,
            'string_exceed_min_length': App.config.i18n.QUERY_BUILDER_ERRORS_STRING_EXCEED_MIN_LENGTH,
            'string_exceed_max_length': App.config.i18n.QUERY_BUILDER_ERRORS_STRING_EXCEED_MAX_LENGTH,
            'string_invalid_format': App.config.i18n.QUERY_BUILDER_ERRORS_STRING_INVALID_FORMAT,
            'number_nan': App.config.i18n.QUERY_BUILDER_ERRORS_NUMBER_NAN,
            'number_not_integer': App.config.i18n.QUERY_BUILDER_ERRORS_NUMBER_NOT_INTEGER,
            'number_not_double': App.config.i18n.QUERY_BUILDER_ERRORS_NUMBER_NOT_DOUBLE,
            'number_exceed_min': App.config.i18n.QUERY_BUILDER_ERRORS_NUMBER_EXCEED_MIN,
            'number_exceed_max': App.config.i18n.QUERY_BUILDER_ERRORS_NUMBER_EXCEED_MAX,
            'number_wrong_step': App.config.i18n.QUERY_BUILDER_ERRORS_NUMBER_WRONG_STEP,
            'datetime_empty': App.config.i18n.QUERY_BUILDER_ERRORS_DATETIME_EMPTY,
            'datetime_invalid': App.config.i18n.QUERY_BUILDER_ERRORS_DATETIME_INVALID,
            'datetime_exceed_min': App.config.i18n.QUERY_BUILDER_ERRORS_DATETIME_EXCEED_MIN,
            'datetime_exceed_max': App.config.i18n.QUERY_BUILDER_ERRORS_DATETIME_EXCEED_MAX,
            'boolean_not_valid': App.config.i18n.QUERY_BUILDER_ERRORS_BOOLEAN_NOT_VALID,
            'operator_not_multiple': App.config.i18n.QUERY_BUILDER_ERRORS_OPERATOR_NOT_MULTIPLE
        }
    }});

    var dateInput = function(rule,input_name){
        return '<input type="date" name="'+input_name+'"/>';
    };

    var stringOperators = ['equal', 'not_equal', 'contains', 'not_contains', 'begins_with', 'not_begins_with', 'ends_with', 'not_ends_with'];
    var dateOperators = ['equal', 'not_equal', 'less', 'less_or_equal', 'greater', 'greater_or_equal'];
    var lovOperators = ['equal', 'not_equal'];

    var filters = [];

    // Part Master
    filters.push({
        id: 'pm.name',
        label: App.config.i18n.PART_NAME,
        type: 'string',
        operators: stringOperators,
        optgroup:App.config.i18n.QUERY_GROUP_PART_MASTER,
        realType:'string'
    });

    filters.push({
        id: 'pm.number',
        label: App.config.i18n.PART_NUMBER,
        type: 'string',
        operators: stringOperators,
        optgroup:App.config.i18n.QUERY_GROUP_PART_MASTER,
        realType:'partNumber'
    });

    filters.push({
        id: 'pm.type',
        label: App.config.i18n.TYPE,
        type: 'string',
        operators: stringOperators,
        optgroup:App.config.i18n.QUERY_GROUP_PART_MASTER,
        realType:'string'
    });

    filters.push({
        id: 'author.login',
        label: App.config.i18n.AUTHOR_LOGIN,
        type: 'string',
        operators: stringOperators,
        optgroup:App.config.i18n.QUERY_GROUP_AUTHOR,
        realType:'string'
    });

    filters.push({
        id: 'author.name',
        label: App.config.i18n.AUTHOR_NAME,
        type: 'string',
        operators: stringOperators,
        optgroup:App.config.i18n.QUERY_GROUP_AUTHOR,
        realType:'string'
    });

    // Part Revision

    filters.push({
        id: 'pr.version',
        label: App.config.i18n.VERSION,
        type: 'string',
        operators: stringOperators,
        optgroup:App.config.i18n.QUERY_GROUP_PART_REVISION,
        realType:'string'
    });

    filters.push({
        id: 'pr.modificationDate',
        label: App.config.i18n.MODIFICATION_DATE,
        type: 'date',
        input:dateInput,
        operators: dateOperators,
        optgroup:App.config.i18n.QUERY_GROUP_PART_REVISION,
        realType:'date'
    });

    filters.push({
        id: 'pr.creationDate',
        label: App.config.i18n.CREATION_DATE,
        type: 'date',
        input:dateInput,
        operators: dateOperators,
        optgroup:App.config.i18n.QUERY_GROUP_PART_REVISION,
        realType:'date'
    });

    filters.push({
        id: 'pr.checkoutDate',
        label: App.config.i18n.CHECKOUT_DATE,
        type: 'date',
        input:dateInput,
        operators: dateOperators,
        optgroup:App.config.i18n.QUERY_GROUP_PART_REVISION,
        realType:'date'
    });

    filters.push({
        id: 'pr.checkinDate',
        label: App.config.i18n.CHECKIN_DATE,
        type: 'date',
        input:dateInput,
        operators: dateOperators,
        optgroup:App.config.i18n.QUERY_GROUP_PART_REVISION,
        realType:'date'
    });

    filters.push({
        id: 'pr.lifeCycleState',
        label: App.config.i18n.LIFECYCLE_STATE,
        type: 'string',
        operators: stringOperators,
        optgroup:App.config.i18n.QUERY_GROUP_PART_REVISION,
        realType:'string'
    });

    filters.push({
        id: 'pr.status',
        label: App.config.i18n.STATUS,
        type: 'string',
        operators: stringOperators,
        optgroup:App.config.i18n.QUERY_GROUP_PART_REVISION,
        realType:'string'
    });

    // Product instances

    filters.push({
        id: 'pi.serial',
        label: App.config.i18n.SERIAL_NUMBER,
        type: 'string',
        operators: stringOperators,
        optgroup:App.config.i18n.QUERY_GROUP_PRODUCT_INSTANCE,
        realType:'string'
    });

    filters.push({
        id: 'pi.name',
        label: App.config.i18n.PRODUCT_NAME,
        type: 'string',
        operators: stringOperators,
        optgroup:App.config.i18n.QUERY_GROUP_PRODUCT_INSTANCE,
        realType:'string'
    });


    var fields = [];

    var types = {
        BOOLEAN: 'boolean',
        DATE: 'date',
        NUMBER: 'double',
        TEXT: 'string',
        URL: 'string',
        LOV: 'lov'
    };

    var groups = [
        {id: 'pm', name: App.config.i18n.QUERY_GROUP_PART_MASTER},
        {id: 'pr', name: App.config.i18n.QUERY_GROUP_PART_REVISION},
        {id: 'pi', name: App.config.i18n.QUERY_GROUP_PRODUCT_INSTANCE},
        {id: 'author', name: App.config.i18n.QUERY_GROUP_AUTHOR},
        {id: 'attr-TEXT', name: App.config.i18n.QUERY_GROUP_ATTRIBUTE_STRING},
        {id: 'attr-URL', name: App.config.i18n.QUERY_GROUP_ATTRIBUTE_URL},
        {id: 'attr-LOV', name: App.config.i18n.QUERY_GROUP_ATTRIBUTE_LOV},
        {id: 'attr-NUMBER', name: App.config.i18n.QUERY_GROUP_ATTRIBUTE_NUMBER},
        {id: 'attr-DATE', name: App.config.i18n.QUERY_GROUP_ATTRIBUTE_DATE},
        {id: 'attr-BOOLEAN', name: App.config.i18n.QUERY_GROUP_ATTRIBUTE_BOOLEAN}
    ];

    filters.map(function(filter){
        fields.push({
            name:filter.label,
            value:filter.id,
            group:filter.id.substring(0,filter.id.indexOf('.'))
        });
    });

    return {
        filters : filters,
        fields : fields,
        groups : groups,
        types : types,

        stringOperators: stringOperators,
        dateOperators : dateOperators,
        lovOperators : lovOperators
    };

});