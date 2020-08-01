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
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
var crypto_1 = __importDefault(require("crypto"));
var app_1 = require("../app");
var ResearcherRepository_1 = require("../repository/ResearcherRepository");
var TypeRepository_1 = require("../repository/TypeRepository");
var CredentialRepository_1 = require("../repository/CredentialRepository");
function SecurityContext() {
    return Promise.resolve({ type: "", id: "" });
}
exports.SecurityContext = SecurityContext;
function ActionContext() {
    return Promise.resolve({ type: "", id: "" });
}
exports.ActionContext = ActionContext;
var rootPassword;
function _verify(authHeader, type /* 'root' = [] */, auth_value) {
    return __awaiter(this, void 0, void 0, function () {
        var e_1, random, authStr, cosignData, auth, sub_auth_value, from, to, result, result, _a, _b, _c, _d, from, to, _e, _f;
        return __generator(this, function (_g) {
            switch (_g.label) {
                case 0:
                    if (!(rootPassword === undefined)) return [3 /*break*/, 4];
                    _g.label = 1;
                case 1:
                    _g.trys.push([1, 3, , 4]);
                    return [4 /*yield*/, app_1.Database.use("root").get("#master_config")];
                case 2:
                    rootPassword = (_g.sent()).data.password;
                    return [3 /*break*/, 4];
                case 3:
                    e_1 = _g.sent();
                    random = crypto_1.default.randomBytes(32).toString("hex");
                    console.dir("Because no master configuration could be located, a temporary root password was generated for this session.");
                    console.table({ "Root Password": random });
                    rootPassword = app_1.Encrypt(random, "AES256");
                    return [3 /*break*/, 4];
                case 4:
                    authStr = ((authHeader !== null && authHeader !== void 0 ? authHeader : "")).replace("Basic", "").trim();
                    cosignData = authStr.startsWith("LAMP") ? JSON.parse(app_1.Decrypt(authStr.slice(4)) || "") : undefined;
                    if (cosignData !== undefined)
                        // FIXME !?
                        authStr = Object.values(cosignData.cosigner).join(":");
                    else
                        authStr = authStr.indexOf(":") >= 0 ? authStr : Buffer.from(authStr, "base64").toString();
                    auth = authStr.split(":", 2);
                    // If no authorization is provided, ask for something.
                    if (auth.length !== 2 || !auth[1]) {
                        throw new Error("401.missing-credentials");
                    }
                    sub_auth_value = undefined;
                    if (!(!auth_value && !["root", "admin"].includes(auth[0]))) return [3 /*break*/, 5];
                    throw new Error("403.security-context-requires-root-scope");
                case 5:
                    if (!(["root", "admin"].includes(auth[0]) && auth[1] !== app_1.Decrypt(rootPassword, "AES256"))) return [3 /*break*/, 6];
                    throw new Error("403.invalid-credentials");
                case 6:
                    if (!!(["root", "admin"].includes(auth[0]) && auth[1] === app_1.Decrypt(rootPassword, "AES256"))) return [3 /*break*/, 15];
                    from = auth[0], to = auth_value;
                    if (!(TypeRepository_1.TypeRepository._self_type(from) === "Participant")) return [3 /*break*/, 8];
                    return [4 /*yield*/, app_1.SQL.request().query("\n\t            SELECT Password \n\t            FROM Users\n\t            WHERE IsDeleted = 0 AND StudyId = '" + app_1.Encrypt(from) + "';\n\t\t\t")];
                case 7:
                    result = (_g.sent()).recordset;
                    if (result.length === 0 || app_1.Decrypt(result[0]["Password"], "AES256") !== auth[1])
                        throw new Error("403.invalid-credentials"); /* authorization-failed */
                    return [3 /*break*/, 12];
                case 8:
                    if (!(TypeRepository_1.TypeRepository._self_type(from) === "Researcher")) return [3 /*break*/, 10];
                    return [4 /*yield*/, app_1.SQL.request().query("\n\t            SELECT AdminID, Password \n\t            FROM Admin\n\t            WHERE IsDeleted = 0 AND AdminID = '" + ResearcherRepository_1.ResearcherRepository._unpack_id(from).admin_id + "';\n\t\t\t")];
                case 9:
                    result = (_g.sent()).recordset;
                    if (result.length === 0 || app_1.Decrypt(result[0]["Password"], "AES256") !== auth[1])
                        throw new Error("403.invalid-credentials");
                    return [3 /*break*/, 12];
                case 10:
                    _a = auth;
                    _b = 0;
                    return [4 /*yield*/, CredentialRepository_1.CredentialRepository._find(auth[0], auth[1] || "*" /* FIXME: force password matching */)];
                case 11:
                    _a[_b] = from = _g.sent();
                    _g.label = 12;
                case 12:
                    // Patch in the special-cased "me" to the actual authenticated credential.
                    if (to === "me")
                        sub_auth_value = to = auth[0];
                    /* Check if `to` and `from` are the same object. */
                    _c = type.includes("self") &&
                        from !== to &&
                        /* FIXME: Check if the immediate parent type of `to` is found in `from`'s inheritance tree. */
                        type.includes("sibling") &&
                        TypeRepository_1.TypeRepository._parent_type(from || "").indexOf(TypeRepository_1.TypeRepository._parent_type(to || "")[0]) < 0 &&
                        /* Check if `from` is actually the parent ID of `to` matching the same type as `from`. */
                        type.includes("parent");
                    if (!_c) 
                    /* Check if `to` and `from` are the same object. */
                    return [3 /*break*/, 14];
                    _d = from;
                    return [4 /*yield*/, TypeRepository_1.TypeRepository._parent_id(to || "", TypeRepository_1.TypeRepository._self_type(from || ""))];
                case 13:
                    _c = _d !== (_g.sent());
                    _g.label = 14;
                case 14:
                    // FIXME: R vs P?
                    // Handle whether we require the parameter to be [[[self], a sibling], or a parent].
                    if (_c) {
                        /* We've given the authorization enough chances... */
                        throw new Error("403.security-context-out-of-scope");
                    }
                    _g.label = 15;
                case 15:
                    if (!!!cosignData) return [3 /*break*/, 18];
                    from = cosignData.identity.from;
                    to = auth_value;
                    // Patch in the special-cased "me" to the actual authenticated credential.
                    if (auth_value /* to */ === "me")
                        sub_auth_value = to = cosignData.identity.to;
                    /* Check if `to` and `from` are the same object. */
                    _e = type.includes("self") &&
                        from !== to &&
                        /* FIXME: Check if the immediate parent type of `to` is found in `from`'s inheritance tree. */
                        type.includes("sibling") &&
                        TypeRepository_1.TypeRepository._parent_type(from || "").indexOf(TypeRepository_1.TypeRepository._parent_type(to || "")[0]) < 0 &&
                        /* Check if `from` is actually the parent ID of `to` matching the same type as `from`. */
                        type.includes("parent");
                    if (!_e) 
                    /* Check if `to` and `from` are the same object. */
                    return [3 /*break*/, 17];
                    _f = from;
                    return [4 /*yield*/, TypeRepository_1.TypeRepository._parent_id(to || "", TypeRepository_1.TypeRepository._self_type(from || ""))];
                case 16:
                    _e = _f !== (_g.sent());
                    _g.label = 17;
                case 17:
                    // FIXME: R vs P?
                    // Handle whether we require the parameter to be [[[self], a sibling], or a parent].
                    if (_e) {
                        /* We've given the authorization enough chances... */
                        throw new Error("403.security-context-out-of-scope");
                    }
                    _g.label = 18;
                case 18:
                    // There shouldn't be any "me" anymore -- unless we're root.
                    if (cosignData === undefined && sub_auth_value === undefined && auth_value /* to */ === "me")
                        throw new Error("400.context-substitution-failed");
                    return [2 /*return*/, sub_auth_value || auth_value];
            }
        });
    });
}
exports._verify = _verify;
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiU2VjdXJpdHkuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi9zcmMvc2VydmljZS9TZWN1cml0eS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7OztBQUFBLGtEQUEyQjtBQUMzQiw4QkFBd0Q7QUFDeEQsMkVBQXlFO0FBQ3pFLCtEQUE2RDtBQUM3RCwyRUFBeUU7QUFFekUsU0FBZ0IsZUFBZTtJQUM3QixPQUFPLE9BQU8sQ0FBQyxPQUFPLENBQUMsRUFBRSxJQUFJLEVBQUUsRUFBRSxFQUFFLEVBQUUsRUFBRSxFQUFFLEVBQUUsQ0FBQyxDQUFBO0FBQzlDLENBQUM7QUFGRCwwQ0FFQztBQUVELFNBQWdCLGFBQWE7SUFDM0IsT0FBTyxPQUFPLENBQUMsT0FBTyxDQUFDLEVBQUUsSUFBSSxFQUFFLEVBQUUsRUFBRSxFQUFFLEVBQUUsRUFBRSxFQUFFLENBQUMsQ0FBQTtBQUM5QyxDQUFDO0FBRkQsc0NBRUM7QUFFRCxJQUFJLFlBQW9CLENBQUE7QUFFeEIsU0FBc0IsT0FBTyxDQUMzQixVQUE4QixFQUM5QixJQUEwQyxDQUFDLGlCQUFpQixFQUM1RCxVQUFtQjs7Ozs7O3lCQUdmLENBQUEsWUFBWSxLQUFLLFNBQVMsQ0FBQSxFQUExQix3QkFBMEI7Ozs7b0JBRVQscUJBQU0sY0FBUSxDQUFDLEdBQUcsQ0FBQyxNQUFNLENBQUMsQ0FBQyxHQUFHLENBQUMsZ0JBQWdCLENBQUMsRUFBQTs7b0JBQWpFLFlBQVksR0FBSSxDQUFDLFNBQWdELENBQVMsQ0FBQyxJQUFJLENBQUMsUUFBUSxDQUFBOzs7O29CQUVsRixNQUFNLEdBQUcsZ0JBQU0sQ0FBQyxXQUFXLENBQUMsRUFBRSxDQUFDLENBQUMsUUFBUSxDQUFDLEtBQUssQ0FBQyxDQUFBO29CQUNyRCxPQUFPLENBQUMsR0FBRyxDQUFDLDZHQUNjLENBQUMsQ0FBQTtvQkFDM0IsT0FBTyxDQUFDLEtBQUssQ0FBQyxFQUFFLGVB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