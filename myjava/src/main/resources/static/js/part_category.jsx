var partCategoriesDOM = null;
function partCategories(parentId, parentParentId, e) {
    if (typeof parentId == "undefined") parentId = 0;
    if (typeof parentParentId == "undefined" || typeof parentParentId == "object") parentParentId = 0;
    if (typeof e != "undefined") e.preventDefault();

    if (partCategoriesDOM == null) {
        ReactDOM.render(
            <PartCategories parentId={parentId} parentParentId={parentParentId}/>
            , document.getElementById("main")
        );
    } else {
        partCategoriesAjax(parentId);
    }

}

class PartCategories extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            parentId : props.parentId,
            parentParentId : props.parentParentId,
            movePartCategoryIdFrom : props.movePartCategoryIdFrom,
            items : []
        };
    }

    setState(state) {
        super.setState(state);
    }

    componentDidMount() {
        partCategoriesDOM = this;
        partCategoriesAjax(this.state.parentId);
    }

    componentWillUnmount() {
        partCategoriesDOM = null;
    }

    render() {
        return (
            <PartCategoriesRoot
                parentId={this.state.parentId}
                parentParentId={this.state.parentParentId}
                moveParentCategoryIdFrom={this.state.movePartCategoryIdFrom}
                items={this.state.items}
            />
        );
    }
}

function PartCategoriesRoot(props) {
    return (
        <div className={'panel panel-default'}>
            {loggedIn ? (
            <div className={'panel-heading'} style={{position:'fixed'}}>
                <PartCategoriesFloatLayer parentId={props.parentId} parentParentId={props.parentParentId} movePartCategoryIdFrom={props.movePartCategoryIdFrom}/>
            </div>
            ) : (<div/>)}
            <div className={'panel-body'}>
                <PartCategoriesBodyTable items={props.items} />
            </div>

            <PartCategoriesModal parentId={props.parentId}/>
        </div>
    );
}

function PartCategoriesFloatLayer(props) {
    return (
        <div>
            <button name={'goUp'} className={'btn btn-primary'} style={{display:(props.parentId == 0 ? 'none' : 'inline-block')}} onClick={(e) => partCategories(props.parentParentId, e)}>&lt;</button>
            <button className={'btn btn-primary'} onClick={(e) => e.preventDefault()} data-toggle="modal" data-target="#myModal">+</button>
            <button name={'moveHere'} className={'btn btn-primary'} style={{display:(props.movePartCategoryIdFrom == null ? 'none' : 'inline-block')}} onClick={(e) => movePartCategoryHere(props.parentId, e)}>Paste</button>
            <button name={'moveHere'} className={'btn btn-danger'} style={{display:(props.movePartCategoryIdFrom == null ? 'none' : 'inline-block')}} onClick={(e) => movePartCategoryCancel(e)}>Cancel</button>
        </div>
    );
}

function PartCategoriesBodyTable(props) {
    return (
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
            {props.items.map(function(item, key) {
                var repImgs = [];
                if(typeof item.repImgs != "undefined") {
                    repImgs = JSON.parse(item.repImgs);
                }

                return <PartCategoriesElement
                    key={key}
                    item={item}
                    movePartCategoryIdFrom={movePartCategoryIdFrom}
                    repImgs={repImgs}/>
            })}
            </tbody>
        </table>
    );
}

function PartCategoriesElement(props) {
    const item = props.item;
    const movePartCategoryIdFrom = props.movePartCategoryIdFrom;
    const repImgs = props.repImgs;

    return (
        <tr>
            <td>{item.blCategoryId}</td>
            <td>
                {item.depth} ({item.parentId})
                <button className={'btn btn-primary btn-sm btn-block'} onClick={(e) => movePartCategory(item.id, e)}>GoTo</button>
                {item.blCategoryId == null ? <button name={'moveHere'} className={'btn btn-primary btn-sm btn-block'} style={{display:(movePartCategoryIdFrom == null ? 'none' : 'block')}} onClick={(e) => movePartCategoryHere(item.id, e)}>Paste</button> : ''}
            </td>
            <td>
                {item.setQty} / ({item.parts})
                {
                    item.blCategoryId == null ?
                        <button className={'btn btn-block btn-default'} onClick={(e) => partCategories(item.id, item.parentId, e)}>{item.name}</button> :
                        <button className={'btn btn-block btn-info'} onClick={(e) => partList(item.blCategoryId, item.parentId, e)}>{item.name}</button>
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
    );
}

function PartCategoriesModal(props) {
    const parentId = props.parentId;
    return (
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
                    <button type="submit" className="btn btn-primary" onClick={(e) => newPartCategory($("#partCategoryForm"), e)}>생성하기</button>
                    <button type="button" className="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>

        </div>
    </div>
    );
}

function partCategoriesAjax(parentId) {
    $.ajax({
        cache : false,
        url:"/partCategories",
        type : "GET",
        dataType : "json",
        data : {parentId : parentId},
        contentType: "application/json;charset=UTF-8",
        async : true
    }).done(function(data) {
        partCategoriesDOM.setState({
            parentId : parentId,
            items:data
        });
    });
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
        async : true
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
        async : true
    }).always(function(data) {
        alert(data.responseText);
        $("#myModal").modal("toggle");
        partCategories($("#partCategoryForm [name=parentId]").val());
    });

}