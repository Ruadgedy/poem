<%@page contentType="text/html; UTF-8" isELIgnored="false" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>唐诗-宋词后台管理系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/boot/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/boot/css/ui.jqgrid-bootstrap.css">
    <script src="${pageContext.request.contextPath}/boot/js/jquery-3.4.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/boot/jqgrid/jquery.jqGrid.min.js"></script>
    <script src="${pageContext.request.contextPath}/boot/jqgrid/grid.locale-cn.js"></script>
    <script src="${pageContext.request.contextPath}/boot/js/bootstrap.js"></script>
    <script>
        $(function () {
            //初始化
            $("#contentList").jqGrid({
                styleUI: "Bootstrap",
                caption: "唐诗宋词列表",
                autowidth: true,
                height: 380,
                url: "${pageContext.request.contextPath}/poem/findByPage",
                datatype: "json",
                colNames: ["id", "诗词名", "作者", "类型", "来源", "内容", "作者简介"],
                colModel: [
                    {name: "id", editable: true,},
                    {name: "name", editable: true, width: 100},
                    {name: "author", editable: true, width: 40},
                    {name: "type", editable: true, width: 40},
                    {name: "origin", editable: true, width: 40},
                    {name: "content", editable: true, width: 400},
                    {name: "authordes", editable: true, width: 400},
                ],
                pager: "#pager",
                page: 1,
                rowNum: 10,
                rowList: [10, 15, 30, 40, 50, 70],
                viewrecords: true
            }).jqGrid("navGrid", "#pager", {edit: true, add: true, del: true, search: true, refresh: true});


            //初始化热词
            initHotRemoteDic();

            // 添加热词
            $("#saveDic").click(function () {
                // 获取热词
                var keyword = $.trim($("#remotekeyword").val());
                if (keyword.length === 0){
                    alert("请输入热词");
                    return false;
                }
                // 热词合法，添加热词
                $.post("${pageContext.request.contextPath}/dic/save", {keyword: keyword}, (result)=>{
                    console.log(result);
                    if (result.success) {
                        initHotRemoteDic();
                    } else {
                        alert(result.message);
                    }
                })
            });


            //获取排行榜
            $.post("${pageContext.request.contextPath}/dic/findRedisKeywords", (results)=>{
                $.each(results, (i, value)=>{
                    var button = $("<button/>").addClass("btn btn-primary").css("margin-right", "4px").html("&nbsp;" + value.value + "&nbsp;&nbsp;");
                    var span = $("<span/>").addClass("badge").text(parseFloat(value.score).toFixed(1));
                    if (value.score >= 10){
                        span.css({"color": "red"});
                    }
                    button.append(span);
                    $("#rediskeywordlists").append(button);
                });
            }, "JSON");


            //清空所有文档
            $("#flushDocuments").click(() => {
                if (window.confirm("此操作不能恢复,确定清空所有文档?")) {
                    $.post("${pageContext.request.contextPath}/poem/deleteAll", (result) => {
                        if (result.success) {
                            alert(result.msg);

                        } else {
                            alert(result.msg);
                        }
                    });
                }
            });



            //调整IDEA支持ES6 Preferences | Languages & Frameworks | JavaScript  ===> ECMAscript6
            //function(){}  ==   ()=>{}
            //基础数据重建索引

            $("#createIndex").click(() => {
                if (window.confirm("确定要重建索引吗?重建索引需要一段时间,请耐心等待!!!")) {
                    $.get("${pageContext.request.contextPath}/poem/saveAll", (result) => {
                        if (result.success) {
                            alert(result.msg)
                        } else {
                            alert(res.msg);
                        }
                    }, "JSON");
                }
            });


            //删除指定热词
            $("#keywordLists").on("click", ".close", (e) => {
                console.log(e.currentTarget);//获取触发事件的dom对象  document button
                var keyword = $(e.currentTarget).attr("name");
                $.get("${pageContext.request.contextPath}/dic/delete", {keyword: keyword}, (result) => {
                    console.log(result);
                    if (result.success) {
                        initHotRemoteDic();
                    }
                }, "JSON");
            });

        });


        //封装初始化数据函数
        function initHotRemoteDic() {
            // 获取所有热词
            $.get("${pageContext.request.contextPath}/dic/findAll", function (results) {
                $("#keywordLists").empty();

                $.each(results, (i, keyword)=>{
                    var div = $("<div/>").css({"float": "left", "margin-right": "10px"});

                    if (keyword.length <= 2){
                        div.css("width", "100px");
                        div.addClass("alert alert-success");
                    }

                    if (keyword.length >= 3 && keyword.length < 6){
                        div.css({"width": "140px"});
                        div.addClass("alert alert-warning");
                    }

                    if (keyword.length >= 6){
                        div.css({"width": "180px"});
                        div.addClass("alert alert-danger");
                    }

                    var button = $("<button/>").addClass("close").attr("name", keyword);
                    var span = $("<span/>").html("&times;");
                    button.append(span);
                    div.append(button).append(keyword);
                    $("#keywordLists").append(div);
                });
            }, "JSON");
        }

    </script>
</head>
<body>

<%--导航条--%>
<nav class="navbar navbar-default" style="margin-bottom: 5px">
    <h1>&#9760;</h1>
    <div class="container-fluid">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <a class="navbar-brand" href="#">唐诗-宋词后台管理系统
                <small>V1.0</small>
            </a>
        </div>

        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <button type="button" class="btn btn-danger navbar-btn" id="flushDocuments">清空ES所有文档</button>
            &nbsp;&nbsp;&nbsp;
            <button type="button" style="margin-left: 20px;" class="btn btn-primary navbar-btn" id="createIndex">
                基于基础数据重建ES索引库
            </button>
        </div>
    </div>
</nav>

<%--中心内容栅格系统--%>
<div class="container-fluid">
    <div class="row">
        <div class="col-sm-12" style="padding-left: 0px;padding-right: 0px;">
            <%--jqrid--%>
            <table id="contentList"></table>
            <%--jqgrid 分页工具栏--%>
            <div id="pager"></div>
        </div>
    </div>
    <hr>
    <div class="row">

        <%--redis热词推荐榜--%>
        <div class="col-sm-6">
            <div class="panel panel-default">
                <div class="panel-heading">全网热搜榜:</div>
                <div class="panel-body" id="rediskeywordlists">


                </div>
            </div>
        </div>
        <%--更新远程热词--%>
        <div class="col-sm-6">
            <%--水平表单--%>
            <div class="form-horizontal">
                <div class="form-group">
                    <div class="col-sm-6">
                        <input type="text" class="form-control" id="remotekeyword" placeholder="输入热词...">
                    </div>
                    <div class="col-sm-4">
                        <button class="btn btn-info" id="saveDic">添加远程词典</button>
                    </div>
                </div>
            </div>
            <%--当前系统扩展词--%>
            <div id="keywordLists">

            </div>
        </div>
    </div>
</div>

</body>
</html>
