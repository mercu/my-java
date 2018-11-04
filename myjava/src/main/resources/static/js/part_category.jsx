class PartCategories extends React.Component {
    constructor(props) {
        super(props);
    }

    componentDidMount() {
    }

    render() {
        var element = null;
        var parentId = this.props.parentId;
        var parentParentId = this.props.parentParentId;
        $.ajax({
            cache : false,
            url:"/partCategories",
            type : "GET",
            dataType : "json",
            data : {parentId : this.props.parentId},
            contentType: "application/json;charset=UTF-8",
            async : false
        }).done(function(data){
            element =
                <div className={'panel panel-default'}>
                    <div className={'panel-heading'} style={{position:'fixed'}}>
                        <p>
                            <button name={'goUp'} className={'btn btn-primary'} style={{display:(parentId == 0 ? 'none' : 'inline-block')}} onClick={(e) => partCategories(parentParentId, e)}>상위</button>
                            <button className={'btn btn-primary'} onClick={(e) => e.preventDefault()} data-toggle="modal" data-target="#myModal">카테고리 생성하기</button>
                            <button name={'moveHere'} className={'btn btn-primary'} style={{display:(movePartCategoryIdFrom == null ? 'none' : 'inline-block')}} onClick={(e) => movePartCategoryHere(parentId, e)}>이동-여기로</button>
                            <button name={'moveHere'} className={'btn btn-danger'} style={{display:(movePartCategoryIdFrom == null ? 'none' : 'inline-block')}} onClick={(e) => movePartCategoryCancel(e)}>이동-취소</button>
                        </p>
                    </div>
                    <div className={'panel-body'}>
                        <table className="table table-bordered">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>
                                    LVL<br/>
                                    P_ID
                                </th>
                                <th>NAME</th>
                                <th>REP_IMG</th>
                            </tr>
                            </thead>
                            <tbody>
                            {data.map(function(item, key) {
                                var repImgs = [];
                                if(typeof item.repImgs != "undefined") {
                                    repImgs = JSON.parse(item.repImgs);
                                }

                                return <tr key={key}>
                                    <td>{item.blCategoryId}</td>
                                    <td>
                                        {item.depth} ({item.parentId})
                                        <button className={'btn btn-primary btn-sm btn-block'} onClick={(e) => movePartCategory(item.id, e)}>이동</button>
                                        {item.blCategoryId == null ? <button name={'moveHere'} className={'btn btn-primary btn-sm btn-block'} style={{display:(movePartCategoryIdFrom == null ? 'none' : 'block')}} onClick={(e) => movePartCategoryHere(item.id, e)}>이동-여기로</button> : ''}
                                    </td>
                                    <td>
                                        {item.setQty} / ({item.parts})
                                        {
                                            item.blCategoryId == null ? <button className={'btn btn-block btn-default'} onClick={(e) => partCategories(item.id, item.parentId, e)}>{item.name}</button>
                                                : <button className={'btn btn-block btn-info'} onClick={(e) => partList(item.blCategoryId, item.parentId, e)}>{item.name}</button>
                                        }
                                    </td>
                                    <td>
                                        <div style={{maxWidth: 600}}>
                                        {repImgs.map(function(repImg, imgKey) {
                                            return <img src={repImg} key={imgKey}/>
                                        })}
                                        </div>
                                    </td>
                                </tr>
                                    ;
                            })}
                            </tbody>
                        </table>
                    </div>

                    {/* Modal */}
                    <div id="myModal" className="modal fade" role="dialog">
                        <div className="modal-dialog">

                            {/* Modal content */}
                            <div className="modal-content">
                                <div className="modal-header">
                                    <button type="button" className="close" data-dismiss="modal">&times;</button>
                                    <h4 className="modal-title">카테고리 생성하기</h4>
                                </div>
                                <div className="modal-body">
                                    <form id="partCategoryForm">
                                        <input type="hidden" name="parentId" value={parentId}/>
                                        <div className="form-group">
                                            <label htmlFor="name">카테고리명</label>
                                            <input type="text" className="form-control" name="name"/>
                                        </div>
                                    </form>
                                </div>
                                <div className="modal-footer">
                                    <button type="submit" className="btn btn-primary" onClick={(e) => newPartCategory(this.form, e)}>생성하기</button>
                                    <button type="button" className="btn btn-default" data-dismiss="modal">Close</button>
                                </div>
                            </div>

                        </div>
                    </div>
                </div>
            ;
        });
        return element;
    }
}

var partCategoriesDOM = null;
function partCategories(parentId, parentParentId, e) {
    if (typeof parentId == "undefined") parentId = 0;
    if (typeof parentParentId == "undefined" || typeof parentParentId == "object") parentParentId = 0;
    if (typeof e != "undefined") e.preventDefault();

    partCategoriesDOM = ReactDOM.render(
        <PartCategories parentId={parentId} parentParentId={parentParentId}/>
        , document.getElementById("main")
    );

}

var movePartCategoryIdFrom = null;
function movePartCategory(categoryId, e) {
    if (typeof e != "undefined") e.preventDefault();

    movePartCategoryIdFrom = categoryId;
    $("[name=moveHere]").show();
    // alert("clipped!");
}

function movePartCategoryCancel(e) {
    if (typeof e != "undefined") e.preventDefault();

    movePartCategoryIdFrom = null;
    $("[name=moveHere]").hide();
}

function movePartCategoryHere(parentId, e) {
    if (typeof e != "undefined") e.preventDefault();
    // if(!confirm("여기로 카테고리를 이동하시겠습니까?")) return;

    $.ajax({
        url:"/partCategory/move",
        type : "POST",
        dataType : "json",
        data : {
            "categoryIdFrom" : movePartCategoryIdFrom,
            "parentIdTo" : parentId
        },
        ContentType: "application/json",
        async : false
    }).always(function(data) {
        // alert(data.responseText);
        movePartCategoryIdFrom = null;
        $("[name=moveHere]").hide();
        partCategories($("#partCategoryForm [name=parentId]").val());
    });

}

function newPartCategory(form, e) {
    if (typeof e != "undefined") e.preventDefault();

    $.ajax({
        url:"/partCategory/new",
        type : "POST",
        dataType : "json",
        data : {
            "parentId" : $("#partCategoryForm [name=parentId]").val(),
            "name" : $("#partCategoryForm [name=name]").val()
        },
        ContentType: "application/json",
        async : false
    }).always(function(data) {
        alert(data.responseText);
        $("#myModal").modal("toggle");
        partCategories($("#partCategoryForm [name=parentId]").val());
    });

}