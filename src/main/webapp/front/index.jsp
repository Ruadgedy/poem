<%@page contentType="text/html; UTF-8" isELIgnored="false" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>唐诗-宋词系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/boot/css/bootstrap.min.css">
    <script src="${pageContext.request.contextPath}/boot/js/jquery-3.4.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/boot/js/bootstrap.js"></script>
    <script>
        $(function () {

            //加载所有数据  contextPath = /poem
             $.get("${pageContext.request.contextPath}/poem/findAllKeywords",function (result) {
                 createIndex(result.poems,"#div");
             },"JSON");


            $("#searchBtn").click(function () {
                let content = $("#searchText").val();
                $.get("${pageContext.request.contextPath}/poem/findAllKeywords", {content: content,type:"",author:""}, function (result) {
                    createIndex(result.poems,"#div");
                }, "JSON");
            });


            //a listener event
            $("#div").on("mouseover", "a", (e) => {
                console.log($(e.currentTarget).offset().top);
                console.log($(e.currentTarget).offset().left);
                let id = $(e.currentTarget).attr("poemid");
                let content = $("#content" + id).html();
                let authordes = $("#authordes" + id).html();
                let div = $("<div  />").attr("id", "contenttext").css({
                    "border": "0px red solid",
                    "position": "absolute",
                    "width": "400",
                    "left": $(e.currentTarget).position().left + 30,
                    "top": $(e.currentTarget).position().top + 30,
                    "border-radius": "5px",
                    "display":"none",
                    "z-index":"9999",
                });
                let htmls = `<div class="panel panel-default">
                              <div class="panel-heading">
                                <h3 class="panel-title">正文:</h3>
                              </div>
                              <div class="panel-body">`+content+`</div>
                            </div>
                            <div class="panel panel-default">
                              <div class="panel-heading">
                                <h3 class="panel-title">作者简介:</h3>
                              </div>
                              <div class="panel-body">`+authordes+`</div>
                            </div>`;

                div.html(htmls);
                $(e.currentTarget).parent().parent().parent().parent().append(div);
                $("#contenttext").slideDown(3000);
            }).on("mouseout", "a", () => {
               $("#contenttext").remove();
            });


            //点击类别筛选
            $(".breadcrumb").on("click","li",(e)=>{
                $(e.currentTarget).siblings().removeClass("actives");
                $(e.currentTarget).addClass("actives");
                let type = $(".actives").first().text();
                let author = $(".actives").last().text();
                let content = $("#searchText").val();
                $.get("${pageContext.request.contextPath}/poem/findAllKeywords", {content: content,type:type,author:author}, function (result) {
                    createIndex(result.poems,"#div");
                }, "JSON");
            });






        });




        //封装创建主页函数
        function createIndex(result,list){
            // 移除所有的子节点
            $(list).empty();
            $.each(result, function (i, poem) {
                let div = $("<div />").css({
                    "border": "0px black solid",
                    "float": "left",
                    "width": "280",
                    "height": "70",
                    "margin": "10px 50px 15px 0px",
                    "border-radius": "10px",
                    "position":"relative"
                });
                let ul = $("<ul/>");
                let name = $("<li/>").html("<h4><a href='javascript:;' poemid=" + poem.id + ">" + poem.name + "</h4>");
                let author = $("<li/>").html("<h4>" + poem.author + "·" + poem.type + "</h4>");
                let content = $("<li/>").html(poem.content).css("display", "none").attr("id", "content" + poem.id);
                let authordes = $("<li/>").html(poem.authordes).css("display", "none").attr("id", "authordes" + poem.id);
                //let imgpath = $("<img/>").attr("src", poem.imagePath).css("margin-top", "15px");
                ul.append(name).append(author).append(content).append(authordes);
                div.append(ul);
                $(list).append(div);
            });
        }
    </script>
    <style>
        .breadcrumb > li + li:before {
            content: "|";
        }
        .actives{
            color: #ff0000;
        }
    </style>
</head>
<body>
<div class="container-fluid">

    <div class="row">
        <div class="col-md-4 col-md-offset-4">
            <h1 class="text-center">唐诗-宋词检索系统</h1>
        </div>
    </div>
    <br><br>

    <div class="row">
        <form class="form-horizontal">
            <div class="form-group">
                <label for="searchText" class="col-sm-2 control-label">检索唐诗宋词</label>
                <div class="col-sm-8">
                    <input type="text" class="form-control" id="searchText" placeholder="输入检索条件....">
                </div>
                <div class="col-sm-2">
                    <button type="button" id="searchBtn" class="btn btn-primary">检索</button>
                </div>
            </div>
        </form>
    </div>
    <div class="row">

        <div class="col-sm-8 col-sm-offset-2" style="margin-bottom: 10px;padding-left: 0px;">
            <ul class="breadcrumb" style="padding: 25px;">
                <li class="actives">所有</li>
                <li>唐诗</li>
                <li>宋词</li>
            </ul>
        </div>

        <div class="col-sm-8 col-sm-offset-2" style="padding-left: 0px;">
            <ul class="breadcrumb" style="padding: 25px;">
                <li class="actives">所有</li>
                <li>李白</li>
                <li>韦应物</li>
                <li>李清照</li>
                <li>吴文英</li>
                <li>杜甫</li>
                <li>李商隐</li>
                <li>杜牧</li>
                <li>晏几道</li>
                <li>柳永</li>
                <li>刘长卿</li>
                <li>王维</li>
                <li>孟浩然</li>
            </ul>


        </div>


    </div>
    <div class="row">
        <div class="col-sm-8 col-sm-offset-2">
            <div id="div">


            </div>

        </div>
    </div>
</div>
</body>
</html>
