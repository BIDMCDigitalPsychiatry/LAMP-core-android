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
var app_1 = require("../app");
var Researcher_1 = require("../model/Researcher");
var StudyRepository_1 = require("../repository/StudyRepository");
var TypeRepository_1 = require("../repository/TypeRepository");
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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiUmVzZWFyY2hlclJlcG9zaXRvcnkuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi9zcmMvcmVwb3NpdG9yeS9SZXNlYXJjaGVyUmVwb3NpdG9yeS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7OztBQUFBLDhCQUE4QztBQUk5QyxrREFBZ0Q7QUFDaEQsaUVBQStEO0FBQy9ELCtEQUFpRjtBQUVqRjtJQUFBO0lBOExBLENBQUM7SUE3TEM7O09BRUc7SUFDVyw2QkFBUSxHQUF0QixVQUF1QixVQUt0QjtRQUNDLE9BQU8sZ0NBQWUsQ0FBQyxDQUFPLHVCQUFXLENBQUMsSUFBSSxFQUFFLFVBQVUsQ0FBQyxRQUFRLElBQUksQ0FBQyxDQUFDLENBQUMsQ0FBQTtJQUM1RSxDQUFDO0lBRUQ7O09BRUc7SUFDVywrQkFBVSxHQUF4QixVQUNFLEVBQVU7UUFPVixJQUFNLFVBQVUsR0FBRyxrQ0FBaUIsQ0FBQyxFQUFFLENBQUMsQ0FBQTtRQUN4QyxJQUFJLFVBQVUsQ0FBQyxDQUFDLENBQUMsS0FBVyx1QkFBVyxDQUFDLElBQUk7WUFBRSxNQUFNLElBQUksS0FBSyxDQUFDLHdCQUF3QixDQUFDLENBQUE7UUFDdkYsSUFBTSxNQUFNLEdBQUcsVUFBVSxDQUFDLEtBQUssQ0FBQyxDQUFDLENBQUMsQ0FBQyxHQUFHLENBQUMsVUFBQyxDQUFDLHdCQUFLLE1BQU0sQ0FBQyxLQUFLLENBQUMsQ0FBQyxDQUFDLHVDQUFJLENBQUMsSUFBQSxDQUFDLENBQUE7UUFDbkUsT0FBTztZQUNMLFFBQVEsRUFBRSxNQUFNLENBQUMsQ0FBQyxDQUFDO1NBQ3BCLENBQUE7SUFDSCxDQUFDO0lBRUQ7O09BRUc7SUFDaUIsK0JBQVUsR0FBOUIsVUFBK0IsRUFBVSxFQUFFLElBQWM7Ozs7Z0JBQy9DLFFBQVEsR0FBSyxvQkFBb0IsQ0FBQyxVQUFVLENBQUMsRUFBRSxDQUFDLFNBQXhDLENBQXdDO2dCQUN4RCxR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