/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80023
 Source Host           : localhost:3306
 Source Schema         : wiki

 Target Server Type    : MySQL
 Target Server Version : 80023
 File Encoding         : 65001

 Date: 10/09/2021 11:17:20
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint NOT NULL COMMENT 'id',
  `parent` bigint NOT NULL DEFAULT 0 COMMENT '父id',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '名称',
  `sort` int NULL DEFAULT NULL COMMENT '顺序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '分类' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (100, 0, '前端开发', 100);
INSERT INTO `category` VALUES (101, 100, 'Vue', 101);
INSERT INTO `category` VALUES (102, 100, 'HTML & CSS', 102);
INSERT INTO `category` VALUES (103, 100, '前端脚本语言', 103);
INSERT INTO `category` VALUES (200, 0, 'Java', 200);
INSERT INTO `category` VALUES (201, 200, '基础应用', 201);
INSERT INTO `category` VALUES (202, 200, '框架应用', 202);
INSERT INTO `category` VALUES (300, 0, 'Python', 300);
INSERT INTO `category` VALUES (301, 300, '基础应用', 301);
INSERT INTO `category` VALUES (302, 300, '进阶方向应用', 302);
INSERT INTO `category` VALUES (400, 0, '数据库', 400);
INSERT INTO `category` VALUES (401, 400, 'MySQL', 401);
INSERT INTO `category` VALUES (500, 0, '其它', 500);
INSERT INTO `category` VALUES (501, 500, '服务器', 501);
INSERT INTO `category` VALUES (502, 500, '开发工具', 502);
INSERT INTO `category` VALUES (503, 500, '热门服务端语言', 503);

-- ----------------------------
-- Table structure for content
-- ----------------------------
DROP TABLE IF EXISTS `content`;
CREATE TABLE `content`  (
  `id` bigint NOT NULL COMMENT '文档id',
  `content` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '内容',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文档内容' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of content
-- ----------------------------
INSERT INTO `content` VALUES (1, '<p>1234565</p>');
INSERT INTO `content` VALUES (2, '<pre><code class=\"Java\"><xmp>@Service\npublic class DocServiceImpl implements DocService {\n    private static final Logger LOG = LoggerFactory.getLogger(DocServiceImpl.class);\n\n    @Autowired\n    private DocMapper docMapper;\n\n    @Autowired\n    private ContentMapper contentMapper;\n\n    //雪花算法\n    @Autowired\n    private SnowFlake snowFlake;\n\n    /**\n     * 查询指定ebook的文档\n     * @return\n     * @param ebookId\n     */\n    @Override\n    public List<DocQueryResp> all(Long ebookId) {\n        DocExample docExample = new DocExample();\n        docExample.createCriteria().andEbookIdEqualTo(ebookId);\n        docExample.setOrderByClause(\"sort asc\");\n        List<Doc> docList = docMapper.selectByExample(docExample);\n\n        // 列表复制\n        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);\n        return list;\n    }\n\n\n    /**\n     * 分页查询方法\n     *\n     * @param req\n     * @return\n     */\n    @Deprecated\n    @Override\n    public PageResp<DocQueryResp> list(DocQueryReq req) {\n        DocExample docExample = new DocExample();\n        docExample.setOrderByClause(\"sort asc\");\n        DocExample.Criteria criteria = docExample.createCriteria();\n        PageHelper.startPage(req.getPage(), req.getSize());\n        List<Doc> docList = docMapper.selectByExample(docExample);\n\n        PageInfo<Doc> pageInfo = new PageInfo<>(docList);\n        LOG.info(\"总行数：{}\", pageInfo.getTotal());\n        LOG.info(\"总页数：{}\", pageInfo.getPages());\n\n        // 列表复制\n        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);\n\n        PageResp<DocQueryResp> pageResp = new PageResp();\n        pageResp.setTotal(pageInfo.getTotal());\n        pageResp.setList(list);\n\n        return pageResp;\n    }\n\n    /**\n     * 新增或保存doc\n     * 根据有无id属性来判断是更新还是新增\n     * @param req\n     */\n    @Override\n    public void save(DocSaveReq req) {\n        Doc doc = CopyUtil.copy(req, Doc.class);\n        Content content = CopyUtil.copy(req, Content.class);\n        //因为doc.id=content.id所以这里还是判断doc的id就可以了\n        if (ObjectUtils.isEmpty(req.getId())) {\n            // 新增\n            doc.setId(snowFlake.nextId());\n            docMapper.insert(doc);\n\n            content.setId(doc.getId());\n            contentMapper.insert(content);\n        } else {\n            // 更新\n            docMapper.updateByPrimaryKey(doc);\n            /**\n             * Blob代表富文本字段，如果我们一张表既有大字段又有小字段则\n             * updateByPrimaryKey和updateByPrimaryKeyWithBLOBs两个方法都会生成\n             * updateByPrimaryKey是没有关于大字段的操作的\n             * updateByPrimaryKeyWithBLOBs会带上大字段\n             * 同样的还有selectByExample和selectByExampleWithBLOBs\n             */\n            int count=contentMapper.updateByPrimaryKeyWithBLOBs(content);\n            //更新，如果没有则插入。因为有些时候文档的id有，但是content的我们没有去做\n            if (count==0){\n                contentMapper.insert(content);\n            }\n        }\n    }\n\n    /**\n     * 根据id删除doc\n     * @param id\n     */\n    @Override\n    public void delete(Long id) {\n        docMapper.deleteByPrimaryKey(id);\n    }\n\n    /**\n     * 批量删除doc\n     * @param ids\n     */\n    @Override\n    public void delete(List<String> ids) {\n        DocExample docExample = new DocExample();\n        DocExample.Criteria criteria = docExample.createCriteria();\n        criteria.andIdIn(ids);\n        docMapper.deleteByExample(docExample);\n    }\n\n    @Override\n    public String findContent(Long id) {\n        Content content=contentMapper.selectByPrimaryKey(id);\n        if(ObjectUtils.isEmpty(content)){\n            return \"\";\n        }else{\n            return content.getContent();\n        }\n    }\n}</xmp></code></pre>');
INSERT INTO `content` VALUES (3, '');
INSERT INTO `content` VALUES (4, '');
INSERT INTO `content` VALUES (5, '');
INSERT INTO `content` VALUES (6, '');
INSERT INTO `content` VALUES (90709983590748160, '<p>123</p>');
INSERT INTO `content` VALUES (90710623364714496, '<p>123</p>');
INSERT INTO `content` VALUES (91339308467884032, '<blockquote><h2>什么是 TypeScript？</h2><p>TypeScript 是一种由微软开发的自由和开源的编程语言，它是 JavaScript 的一个超集，扩展了 JavaScript 的语法。</p></blockquote>');

-- ----------------------------
-- Table structure for demo
-- ----------------------------
DROP TABLE IF EXISTS `demo`;
CREATE TABLE `demo`  (
  `id` bigint NOT NULL COMMENT 'id',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '测试' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of demo
-- ----------------------------
INSERT INTO `demo` VALUES (1, '测试');

-- ----------------------------
-- Table structure for doc
-- ----------------------------
DROP TABLE IF EXISTS `doc`;
CREATE TABLE `doc`  (
  `id` bigint NOT NULL COMMENT 'id',
  `ebook_id` bigint NOT NULL DEFAULT 0 COMMENT '电子书id',
  `parent` bigint NOT NULL DEFAULT 0 COMMENT '父id',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '名称',
  `sort` int NULL DEFAULT NULL COMMENT '顺序',
  `view_count` int NULL DEFAULT 0 COMMENT '阅读数',
  `vote_count` int NULL DEFAULT 0 COMMENT '点赞数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文档' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of doc
-- ----------------------------
INSERT INTO `doc` VALUES (1, 2, 0, '文档1', 1, 0, 0);
INSERT INTO `doc` VALUES (2, 2, 1, '文档1.1', 1, 0, 0);
INSERT INTO `doc` VALUES (3, 2, 0, '文档2', 2, 0, 0);
INSERT INTO `doc` VALUES (4, 2, 3, '文档2.1', 1, 0, 0);
INSERT INTO `doc` VALUES (5, 2, 3, '文档2.2', 2, 0, 0);
INSERT INTO `doc` VALUES (6, 2, 5, '文档2.2.1', 1, 0, 0);
INSERT INTO `doc` VALUES (90709983590748160, 3, 0, 'python十大法宝', 1, NULL, NULL);
INSERT INTO `doc` VALUES (91339308467884032, 87011925652803580, 0, 'ts的入门语法', 1, NULL, NULL);

-- ----------------------------
-- Table structure for ebook
-- ----------------------------
DROP TABLE IF EXISTS `ebook`;
CREATE TABLE `ebook`  (
  `id` bigint NOT NULL COMMENT 'id',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '名称',
  `category1_id` bigint NULL DEFAULT NULL COMMENT '分类1',
  `category2_id` bigint NULL DEFAULT NULL COMMENT '分类2',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `cover` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面',
  `doc_count` int NOT NULL DEFAULT 0 COMMENT '文档数',
  `view_count` int NOT NULL DEFAULT 0 COMMENT '阅读数',
  `vote_count` int NOT NULL DEFAULT 0 COMMENT '点赞数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '电子书' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ebook
-- ----------------------------
INSERT INTO `ebook` VALUES (2, 'Vue 入门教程', 100, 101, '零基础入门 Vue 开发，企业级应用开发最佳首选框架', '/image/cover_vue.jpg', 0, 0, 0);
INSERT INTO `ebook` VALUES (3, 'Python 入门教程', 300, 301, '零基础入门 Python 开发，企业级应用开发最佳首选框架', '/image/cover_python.jpg', 0, 0, 0);
INSERT INTO `ebook` VALUES (4, 'Mysql 入门教程', 400, 401, '零基础入门 Mysql 开发，企业级应用开发最佳首选框架', '/image/cover_mysql.jpg', 0, 0, 0);
INSERT INTO `ebook` VALUES (5, 'GO 入门教程', 500, 503, '零基础入门Go 开发，企业级应用开发最佳首选框架', '/image/cover_go.jpg', 0, 0, 0);
INSERT INTO `ebook` VALUES (6, 'Spring Boot 入门教程', 200, 202, '零基础入门Spring Boot 开发，企业级应用开发最佳首选框架', '/image/cover_java.jpg', 0, 0, 0);
INSERT INTO `ebook` VALUES (87011925652803580, 'typescript入门教程', 100, 103, 'ts入门用这个就足够了', '/image/cover1.png', 0, 0, 0);
INSERT INTO `ebook` VALUES (87013799881740290, 'javascript入门教程', 100, 103, '学js，有这一本就够了', '/image/cover2.png', 0, 0, 0);

-- ----------------------------
-- Table structure for ebook_snapshot
-- ----------------------------
DROP TABLE IF EXISTS `ebook_snapshot`;
CREATE TABLE `ebook_snapshot`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `ebook_id` bigint NOT NULL DEFAULT 0 COMMENT '电子书id',
  `date` date NOT NULL COMMENT '快照日期',
  `view_count` int NOT NULL DEFAULT 0 COMMENT '阅读数',
  `vote_count` int NOT NULL DEFAULT 0 COMMENT '点赞数',
  `view_increase` int NOT NULL DEFAULT 0 COMMENT '阅读增长',
  `vote_increase` int NOT NULL DEFAULT 0 COMMENT '点赞增长',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `ebook_id_date_unique`(`ebook_id`, `date`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '电子书快照表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ebook_snapshot
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `login_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登陆名',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `password` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `login_name_unique`(`login_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'test', '测试', 'e9ab782b7db2601fa78e48fa74a3c266');

SET FOREIGN_KEY_CHECKS = 1;
