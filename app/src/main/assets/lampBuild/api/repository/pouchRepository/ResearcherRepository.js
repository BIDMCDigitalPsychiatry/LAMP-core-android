"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
Object.defineProperty(exports, "__esModule", { value: true });
var app_1 = require("../../app");
var Researcher_1 = require("../../model/Researcher");
var StudyRepository_1 = require("../../repository/StudyRepository");
var TypeRepository_1 = require("../../repository/TypeRepository");
var ResearcherRepository = /** @class */ (function () {
    function ResearcherRepository() {
    }
    /**
     *
     */
    ResearcherRepository._pack_id = function (components) {
        return TypeRepository_1.Identifier_pack([Researcher_1.Researcher.name, components.admin_id || 0]);
    };
    /**
     *
     */
    ResearcherRepository._unpack_id = function (id) {
        var components = TypeRepository_1.Identifier_unpack(id);
        if (components[0] !== Researcher_1.Researcher.name)
            throw new Error("400.invalid-identifier");
        var result = components.slice(1).map(function (x) { var _a; return _a = Number.parse(x), (_a !== null && _a !== void 0 ? _a : 0); });
        return {
            admin_id: result[0],
        };
    };
    /**
     *
     */
    ResearcherRepository._parent_id = function (id, type) {
        return __awaiter(this, void 0, void 0, function () {
            var admin_id;
            return __generator(this, function (_a) {
                admin_id = ResearcherRepository._unpack_id(id).admin_id;
                switch (type) {
                    default:
                        return [2 /*return*/, undefined]; // throw new Error('400.invalid-identifier')
                }
                return [2 /*return*/];
            });
        });
    };
    /**
     *
     */
    ResearcherRepository._select = function (
    /**
     *
     */
    id) {
        return __awaiter(this, void 0, void 0, function () {
            var admin_id, result;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        if (!!id && TypeRepository_1.Identifier_unpack(id)[0] === Researcher_1.Researcher.name)
                            admin_id = ResearcherRepository._unpack_id(id).admin_id;
                        else if (!!id)
                            throw new Error("400.invalid-identifier");
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\tSELECT \n                AdminID as id, \n                FirstName AS name, \n                LastName AS lname,\n                Email AS email,\n                (\n                    SELECT \n                        AdminID AS id\n                    FOR JSON PATH, INCLUDE_NULL_VALUES\n                ) AS studies\n            FROM Admin\n            WHERE \n            \tIsDeleted = 0 \n            \t" + (!!admin_id ? "AND AdminID = '" + admin_id + "'" : "") + "\n            FOR JSON PATH, INCLUDE_NULL_VALUES;\n\t\t")];
                    case 1:
                        result = _a.sent();
                        if (result.recordset.length === 0 || result.recordset[0] === null)
                            return [2 /*return*/, []];
                        return [2 /*return*/, result.recordset[0].map(function (raw) {
                                var obj = new Researcher_1.Researcher();
                                obj.id = ResearcherRepository._pack_id({ admin_id: raw.id });
                                obj.name = [app_1.Decrypt(raw.name), app_1.Decrypt(raw.lname)].join(" ");
                                obj.email = app_1.Decrypt(raw.email);
                                obj.studies = raw.studies.map(function (x) { return StudyRepository_1.StudyRepository._pack_id({ admin_id: x.id }); });
                                return obj;
                            })];
                }
            });
        });
    };
    /**
     * Create a `Researcher` with a new object.
     */
    ResearcherRepository._insert = function (
    /**
     * The new object.
     */
    object) {
        return __awaiter(this, void 0, void 0, function () {
            var result, result2;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\tINSERT INTO Admin (\n                Email, \n                FirstName, \n                LastName, \n                CreatedOn, \n                AdminType\n            )\n            OUTPUT INSERTED.AdminID AS id\n\t\t\tVALUES (\n\t\t        '" + app_1.Encrypt(object.email) + "',\n\t\t        '" + app_1.Encrypt(object.name.split(" ")[0]) + "',\n\t\t        '" + app_1.Encrypt(object.name.split(" ").slice(1).join(" ")) + "',\n\t\t        GETDATE(), \n\t\t        2\n\t\t\t);\n\t\t")];
                    case 1:
                        result = _a.sent();
                        if (result.recordset.length === 0)
                            throw new Error("400.create-failed");
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\tINSERT INTO Admin_CTestSettings (\n\t\t\t\tAdminID,\n\t\t\t\tCTestID,\n\t\t\t\tStatus,\n\t\t\t\tNotification\n\t\t\t)\n\t\t\tSELECT\n\t\t\t\t" + result.recordset[0]["id"] + ",\n\t\t\t\tCTestID,\n\t\t\t\t0,\n\t\t\t\t0\n\t\t\tFROM CTest;\n\t\t")
                            // Return the new row's ID.
                        ];
                    case 2:
                        result2 = _a.sent();
                        // Return the new row's ID.
                        return [2 /*return*/, ResearcherRepository._pack_id({ admin_id: result.recordset[0]["id"] })];
                }
            });
        });
    };
    /**
     * Update a `Researcher` with new fields.
     */
    ResearcherRepository._update = function (
    /**
     * The `AdminID` column of the `Admin` table in the LAMP v0.1 DB.
     */
    researcher_id, 
    /**
     * The replacement object or specific fields within.
     */
    object) {
        return __awaiter(this, void 0, void 0, function () {
            var admin_id, updates, result;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        admin_id = ResearcherRepository._unpack_id(researcher_id).admin_id;
                        updates = [];
                        if (!!object.name) {
                            updates.push("FirstName = '" + app_1.Encrypt(object.name.split(" ")[0]) + "'");
                            updates.push("LastName = '" + app_1.Encrypt(object.name.split(" ").slice(1).join(" ")) + "'");
                        }
                        if (!!object.email)
                            updates.push("Email = '" + app_1.Encrypt(object.email) + "'");
                        //if (!!(<any>object).password)
                        //	updates.push(`Password = '${Encrypt((<any>object).password, 'AES256')}'`)
                        if (updates.length == 0)
                            throw new Error("400.updates-failed");
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\tUPDATE Admin SET " + updates.join(", ") + " WHERE AdminID = " + admin_id + ";\n\t\t")];
                    case 1:
                        result = _a.sent();
                        return [2 /*return*/, {}]; //result.recordset[0]
                }
            });
        });
    };
    /**
     * Delete a `Researcher` row.
     */
    ResearcherRepository._delete = function (
    /**
     * The `AdminID` column of the `Admin` table in the LAMP v0.1 DB.
     */
    researcher_id) {
        return __awaiter(this, void 0, void 0, function () {
            var admin_id, result;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        admin_id = ResearcherRepository._unpack_id(researcher_id).admin_id;
                        if (admin_id === 1)
                            throw new Error("400.delete-failed");
                        return [4 /*yield*/, app_1.SQL.request().query("\n\t\t\tUPDATE Admin SET IsDeleted = 1 WHERE AdminID = " + admin_id + " AND IsDeleted = 0;\n\t\t")];
                    case 1:
                        result = _a.sent();
                        if (result.rowsAffected[0] === 0)
                            throw new Error("404.object-not-found");
                        return [2 /*return*/, {}];
                }
            });
        });
    };
    return ResearcherRepository;
}());
exports.ResearcherRepository = ResearcherRepository;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiUmVzZWFyY2hlclJlcG9zaXRvcnkuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi9zcmMvcmVwb3NpdG9yeS9wb3VjaFJlcG9zaXRvcnkvUmVzZWFyY2hlclJlcG9zaXRvcnkudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6Ijs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7QUFBQSxpQ0FBaUQ7QUFJakQscURBQW1EO0FBQ25ELG9FQUFrRTtBQUNsRSxrRUFBb0Y7QUFFcEY7SUFBQTtJQThMQSxDQUFDO0lBN0xDOztPQUVHO0lBQ1csNkJBQVEsR0FBdEIsVUFBdUIsVUFLdEI7UUFDQyxPQUFPLGdDQUFlLENBQUMsQ0FBTyx1QkFBVyxDQUFDLElBQUksRUFBRSxVQUFVLENBQUMsUUFBUSxJQUFJLENBQUMsQ0FBQyxDQUFDLENBQUE7SUFDNUUsQ0FBQztJQUVEOztPQUVHO0lBQ1csK0JBQVUsR0FBeEIsVUFDRSxFQUFVO1FBT1YsSUFBTSxVQUFVLEdBQUcsa0NBQWlCLENBQUMsRUFBRSxDQUFDLENBQUE7UUFDeEMsSUFBSSxVQUFVLENBQUMsQ0FBQyxDQUFDLEtBQVcsdUJBQVcsQ0FBQyxJQUFJO1lBQUUsTUFBTSxJQUFJLEtBQUssQ0FBQyx3QkFBd0IsQ0FBQyxDQUFBO1FBQ3ZGLElBQU0sTUFBTSxHQUFHLFVBQVUsQ0FBQyxLQUFLLENBQUMsQ0FBQyxDQUFDLENBQUMsR0FBRyxDQUFDLFVBQUMsQ0FBQyx3QkFBSyxNQUFNLENBQUMsS0FBSyxDQUFDLENBQUMsQ0FBQyx1Q0FBSSxDQUFDLElBQUEsQ0FBQyxDQUFBO1FBQ25FLE9BQU87WUFDTCxRQUFRLEVBQUUsTUFBTSxDQUFDLENBQUMsQ0FBQztTQUNwQixDQUFBO0lBQ0gsQ0FBQztJQUVEOztPQUVHO0lBQ2lCLCtCQUFVLEdBQTlCLFVBQStCLEVBQVUsRUFBRSxJQUFjOzs7O2dCQUMvQyxRQUFRLEdBQUssb0JBQW9CLENBQUMsVUFBVSxDQUFDLEVBQUUsQ0FBQyxT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