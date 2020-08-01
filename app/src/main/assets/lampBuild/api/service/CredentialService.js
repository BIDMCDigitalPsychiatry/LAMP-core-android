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
var express_1 = require("express");
var CredentialRepository_1 = require("../repository/CredentialRepository");
var Security_1 = require("./Security");
var jsonata_1 = __importDefault(require("jsonata"));
exports.CredentialService = express_1.Router();
exports.CredentialService.get("/type/:type_id/credential", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var type_id, output, _a, e_1;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                type_id = req.params.type_id;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "parent"], type_id)];
            case 1:
                type_id = _b.sent();
                _a = {};
                return [4 /*yield*/, CredentialRepository_1.CredentialRepository._select(type_id)];
            case 2:
                output = (_a.data = _b.sent(), _a);
                output = typeof req.query.transform === "string" ? jsonata_1.default(req.query.transform).evaluate(output) : output;
                res.json(output);
                return [3 /*break*/, 4];
            case 3:
                e_1 = _b.sent();
                if (e_1.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_1.message.split(".")[0]) || 500).json({ error: e_1.message });
                return [3 /*break*/, 4];
            case 4: return [2 /*return*/];
        }
    });
}); });
exports.CredentialService.post("/type/:type_id/credential", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var type_id, credential, output, _a, e_2;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                type_id = req.params.type_id;
                credential = req.body;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "parent"], type_id)];
            case 1:
                type_id = _b.sent();
                _a = {};
                return [4 /*yield*/, CredentialRepository_1.CredentialRepository._insert(type_id, credential)];
            case 2:
                output = (_a.data = _b.sent(), _a);
                res.json(output);
                return [3 /*break*/, 4];
            case 3:
                e_2 = _b.sent();
                if (e_2.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_2.message.split(".")[0]) || 500).json({ error: e_2.message });
                return [3 /*break*/, 4];
            case 4: return [2 /*return*/];
        }
    });
}); });
exports.CredentialService.put("/type/:type_id/credential/:access_key", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var type_id, access_key, credential, output, _a, e_3;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                type_id = req.params.type_id;
                access_key = req.params.access_key;
                credential = req.body;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "parent"], type_id)];
            case 1:
                type_id = _b.sent();
                _a = {};
                return [4 /*yield*/, CredentialRepository_1.CredentialRepository._update(type_id, access_key, credential)];
            case 2:
                output = (_a.data = _b.sent(), _a);
                res.json(output);
                return [3 /*break*/, 4];
            case 3:
                e_3 = _b.sent();
                if (e_3.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_3.message.split(".")[0]) || 500).json({ error: e_3.message });
                return [3 /*break*/, 4];
            case 4: return [2 /*return*/];
        }
    });
}); });
exports.CredentialService.delete("/type/:type_id/credential/:access_key", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var type_id, access_key, output, _a, e_4;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                type_id = req.params.type_id;
                access_key = req.params.access_key;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "parent"], type_id)];
            case 1:
                type_id = _b.sent();
                _a = {};
                return [4 /*yield*/, CredentialRepository_1.CredentialRepository._delete(type_id, access_key)];
            case 2:
                output = (_a.data = _b.sent(), _a);
                res.json(output);
                return [3 /*break*/, 4];
            case 3:
                e_4 = _b.sent();
                if (e_4.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_4.message.split(".")[0]) || 500).json({ error: e_4.message });
                return [3 /*break*/, 4];
            case 4: return [2 /*return*/];
        }
    });
}); });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiQ3JlZGVudGlhbFNlcnZpY2UuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi9zcmMvc2VydmljZS9DcmVkZW50aWFsU2VydmljZS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7OztBQUFBLG1DQUFtRDtBQUVuRCwyRUFBeUU7QUFDekUsdUNBQW9FO0FBQ3BFLG9EQUE2QjtBQUVoQixRQUFBLGlCQUFpQixHQUFHLGdCQUFNLEVBQUUsQ0FBQTtBQUN6Qyx5QkFBaUIsQ0FBQyxHQUFHLENBQUMsMkJBQTJCLEVBQUUsVUFBTyxHQUFZLEVBQUUsR0FBYTs7Ozs7O2dCQUU3RSxPQUFPLEdBQUcsR0FBRyxDQUFDLE1BQU0sQ0FBQyxPQUFPLENBQUE7Z0JBQ3RCLHFCQUFNLGtCQUFPLENBQUMsR0FBRyxDQUFDLEdBQUcsQ0FBQyxlQUFlLENBQUMsRUFBRSxDQUFDLE1BQU0sRUFBRSxRQUFRLENBQUMsRUFBRSxPQUFPLENBQUMsRUFBQTs7Z0JBQTlFLE9BQU8sR0FBRyxTQUFvRSxDQUFBOztnQkFDekQscUJBQU0sMkNBQW9CLENBQUMsT0FBTyxDQUFDLE9BQU8sQ0FBQyxFQUFBOztnQkFBNUQsTUFBTSxJQUFLLE9BQUksR0FBRSxTQUEyQyxLQUFFO2dCQUNsRSxNQUFNLEdBQUcsT0FBTyxHQUFHLENBQUMsS0FBSyxDQUFDLFNBQVMsS0FBSyxRQUFRLENBQUMsQ0FBQyxDQUFDLGlCQUFPLENBQUMsR0FBRyxDQUFDLEtBQUssQ0FBQyxTQUFTLENBQUMsQ0FBQyxRQUFRLENBQUMsTUFBTSxDQUFDLENBQUMsQ0FBQyxDQUFDLE1BQU0sQ0FBQTtnQkFDekcsR0FBRyxDQUFDLElBQUksQ0FBQyxNQUFNLENBQUMsQ0FBQTs7OztnQkFFaEIsSUFBSSxHQUFDLENBQUMsT0FBTyxLQUFLLHlCQUF5QjtvQkFBRSxHQUFHLENBQUMsR0FBRyxDQUFDLGtCQUFrQixFQUFFLHdDQUFvQyxDQUFDLENB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