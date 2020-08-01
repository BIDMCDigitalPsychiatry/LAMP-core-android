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
var TypeRepository_1 = require("../repository/TypeRepository");
var Security_1 = require("./Security");
var jsonata_1 = __importDefault(require("jsonata"));
exports.TypeService = express_1.Router();
exports.TypeService.get("/type/:type_id/parent", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var type_id, output, _a, e_1;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                type_id = req.params.type_id;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], type_id)];
            case 1:
                type_id = _b.sent();
                _a = {};
                return [4 /*yield*/, TypeRepository_1.TypeRepository._parent(type_id)];
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
exports.TypeService.get("/type/:type_id/attachment/:attachment_key?/:index?", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var type_id, attachment_key, index, obj, sequenceObj, shouldIndex, realIndex, output, output, _a, _b, _c, _d, e_2;
    var _e;
    return __generator(this, function (_f) {
        switch (_f.label) {
            case 0:
                _f.trys.push([0, 7, , 8]);
                type_id = req.params.type_id;
                attachment_key = req.params.attachment_key;
                index = req.params.index;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], type_id)];
            case 1:
                type_id = _f.sent();
                if (!(attachment_key !== undefined)) return [3 /*break*/, 3];
                return [4 /*yield*/, TypeRepository_1.TypeRepository._get("a", type_id, attachment_key)
                    // TODO: if obj undefined here, return null instead of throwing 404 error
                ];
            case 2:
                obj = _f.sent();
                sequenceObj = Array.isArray(obj) || typeof obj === "string";
                shouldIndex = index !== undefined &&
                    (typeof obj === "object" || (sequenceObj && (Number.parse(index) !== undefined || index === "length")));
                realIndex = sequenceObj && (_e = Number.parse(index), (_e !== null && _e !== void 0 ? _e : 0)) < 0 ? obj.length + Number.parse(index) : index;
                if (index !== undefined && !shouldIndex)
                    throw new Error("404.specified-index-not-found");
                else if (shouldIndex)
                    obj = obj[realIndex];
                output = { data: obj !== undefined ? obj : null };
                res.json(output);
                return [3 /*break*/, 6];
            case 3:
                _a = {};
                _c = (_b = []).concat;
                return [4 /*yield*/, TypeRepository_1.TypeRepository._list("a", type_id)];
            case 4:
                _d = [_f.sent()];
                return [4 /*yield*/, TypeRepository_1.TypeRepository._list("b", type_id)];
            case 5:
                output = (_a.data = _c.apply(_b, _d.concat([(_f.sent()).map(function (x) { return "dynamic/" + x; })])),
                    _a);
                res.json(output);
                _f.label = 6;
            case 6: return [3 /*break*/, 8];
            case 7:
                e_2 = _f.sent();
                if (e_2.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_2.message.split(".")[0]) || 500).json({ error: e_2.message });
                return [3 /*break*/, 8];
            case 8: return [2 /*return*/];
        }
    });
}); });
exports.TypeService.put("/type/:type_id/attachment/:attachment_key/:target", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var type_id, attachment_key, target, attachment_value, output, _a, e_3;
    return __generator(this, function (_b) {
        switch (_b.label) {
            case 0:
                _b.trys.push([0, 3, , 4]);
                type_id = req.params.type_id;
                attachment_key = req.params.attachment_key;
                target = req.params.target;
                attachment_value = req.body;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], type_id)];
            case 1:
                type_id = _b.sent();
                _a = {};
                return [4 /*yield*/, TypeRepository_1.TypeRepository._set("a", target, type_id, attachment_key, attachment_value)];
            case 2:
                output = (_a.data = (_b.sent())
                    ? {}
                    : null /* error */,
                    _a);
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
exports.TypeService.get("/type/:type_id/attachment/dynamic/:attachment_key", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var type_id, attachment_key, invoke_always, ignore_output, include_logs, result, attachment, output, e_4;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0:
                _a.trys.push([0, 8, , 9]);
                type_id = req.params.type_id;
                attachment_key = req.params.attachment_key;
                invoke_always = req.query.invoke_always;
                ignore_output = req.query.ignore_output;
                include_logs = req.query.include_logs;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], type_id)];
            case 1:
                type_id = _a.sent();
                result = {};
                if (!!!invoke_always) return [3 /*break*/, 5];
                return [4 /*yield*/, TypeRepository_1.TypeRepository._get("b", type_id, attachment_key)];
            case 2:
                attachment = _a.sent();
                return [4 /*yield*/, TypeRepository_1.TypeRepository._invoke(attachment, {})];
            case 3:
                result = _a.sent();
                return [4 /*yield*/, TypeRepository_1.TypeRepository._set("a", attachment.to, attachment.from, attachment.key + "/output", result)];
            case 4:
                _a.sent();
                return [3 /*break*/, 7];
            case 5: return [4 /*yield*/, TypeRepository_1.TypeRepository._get("a", type_id, attachment_key + "/output")];
            case 6:
                // Otherwise, return any cached result available.
                result = _a.sent();
                _a.label = 7;
            case 7:
                output = {
                    data: !!include_logs && !ignore_output
                        ? result
                        : {
                            data: !ignore_output ? result.output : undefined,
                            logs: !!include_logs ? result.logs : undefined,
                        },
                };
                res.json(output);
                return [3 /*break*/, 9];
            case 8:
                e_4 = _a.sent();
                if (e_4.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_4.message.split(".")[0]) || 500).json({ error: e_4.message });
                return [3 /*break*/, 9];
            case 9: return [2 /*return*/];
        }
    });
}); });
exports.TypeService.put("/type/:type_id/attachment/dynamic/:attachment_key/:target", function (req, res) { return __awaiter(void 0, void 0, void 0, function () {
    var type_id_1, attachment_key_1, target_1, attachment_value, invoke_once, result, output, e_5;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0:
                _a.trys.push([0, 2, , 3]);
                type_id_1 = req.params.type_id;
                attachment_key_1 = req.params.attachment_key;
                target_1 = req.params.target;
                attachment_value = req.body;
                invoke_once = req.query.invoke_once;
                return [4 /*yield*/, Security_1._verify(req.get("Authorization"), ["self", "sibling", "parent"], type_id_1)];
            case 1:
                type_id_1 = _a.sent();
                result = null /* error */;
                if (TypeRepository_1.TypeRepository._set("b", target_1, type_id_1, attachment_key_1, attachment_value)) {
                    // If needed, invoke the attachment now.
                    if (!!invoke_once) {
                        TypeRepository_1.TypeRepository._invoke(attachment_value, {}).then(function (y) {
                            TypeRepository_1.TypeRepository._set("a", target_1, type_id_1, attachment_key_1 + "/output", y);
                        });
                    }
                    result = {};
                }
                output = { data: result };
                res.json(output);
                return [3 /*break*/, 3];
            case 2:
                e_5 = _a.sent();
                if (e_5.message === "401.missing-credentials")
                    res.set("WWW-Authenticate", "Basic realm=\"LAMP\" charset=\"UTF-8\"");
                res.status(parseInt(e_5.message.split(".")[0]) || 500).json({ error: e_5.message });
                return [3 /*break*/, 3];
            case 3: return [2 /*return*/];
        }
    });
}); });
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiVHlwZVNlcnZpY2UuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi9zcmMvc2VydmljZS9UeXBlU2VydmljZS50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7OztBQUFBLG1DQUFtRDtBQUVuRCwrREFBNkQ7QUFDN0QsdUNBQW9FO0FBQ3BFLG9EQUE2QjtBQUVoQixRQUFBLFdBQVcsR0FBRyxnQkFBTSxFQUFFLENBQUE7QUFDbkMsbUJBQVcsQ0FBQyxHQUFHLENBQUMsdUJBQXVCLEVBQUUsVUFBTyxHQUFZLEVBQUUsR0FBYTs7Ozs7O2dCQUVuRSxPQUFPLEdBQUcsR0FBRyxDQUFDLE1BQU0sQ0FBQyxPQUFPLENBQUE7Z0JBQ3RCLHFCQUFNLGtCQUFPLENBQUMsR0FBRyxDQUFDLEdBQUcsQ0FBQyxlQUFlLENBQUMsRUFBRSxDQUFDLE1BQU0sRUFBRSxTQUFTLEVBQUUsUUFBUSxDQUFDLEVBQUUsT0FBTyxDQUFDLEVBQUE7O2dCQUF6RixPQUFPLEdBQUcsU0FBK0UsQ0FBQTs7Z0JBQ3BFLHFCQUFNLCtCQUFjLENBQUMsT0FBTyxDQUFDLE9BQU8sQ0FBQyxFQUFBOztnQkFBdEQsTUFBTSxJQUFLLE9BQUksR0FBRSxTQUFxQyxLQUFFO2dCQUM1RCxNQUFNLEdBQUcsT0FBTyxHQUFHLENBQUMsS0FBSyxDQUFDLFNBQVMsS0FBSyxRQUFRLENBQUMsQ0FBQyxDQUFDLGlCQUFPLENBQUMsR0FBRyxDQUFDLEtBQUssQ0FBQyxTQUFTLENBQUMsQ0FBQyxRQUFRLENBQUMsTUFBTSxDQUFDLENBQUMsQ0FBQyxDQUFDLE1BQU0sQ0FBQTtnQkFDekcsR0FBRyxDQUFDLElBQUksQ0FBQyxNQUFNLENBQUMsQ0FBQTs7OztnQkFFaEIsSUFBSSxHQUFDLENBQUMsT0FBTyxLQUFLLHlCQUF5QjtvQkFBRSxHQUFHLENBQUMsR0FBRyxDQUFDLGtCQUFrQixFQUFFLHdDQUFvQyxDQUFDLENBQUE7Z0JB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