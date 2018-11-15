var partCategoriesDOM = null;
function partCategories(parentId, parentParentId, e) {
    if (typeof parentId == "undefined") parentId = 0;
    if (typeof parentParentId == "undefined" || typeof parentParentId == "object") parentParentId = 0;
    if (typeof e != "undefined") e.preventDefault();

    if (partCategoriesDOM == null) {
        ReactDOM.render(
            <PartCategories parentId={parentId} parentParentId={parentParentId}/>
            , document.getElementById("partCategories")
        );
    } else {
        partCategoriesAjax(parentId);
    }
    $("#partList").addClass("hide");
    $("#partCategories").removeClass("hide");

}

function partCategoriesAjax(parentId) {
    $.ajax({
        url:"/partCategories",
        type : "GET",
        dataType : "json",
        data : {parentId : parentId},
        contentType: "application/json;charset=UTF-8",
        async : true
    }).done(function(data) {
        if (data.parentCategory === undefined) {
            data.parentCategory = {id : 0, parentId : 0};
        }
        partCategoriesDOM.setState({
            parentId : data.parentCategory.id,
            parentParentId : data.parentCategory.parentId,
            items : data.partCategories
        });
    });
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
            <PartCategoriesFloatLayer parentId={props.parentId}
                parentParentId={props.parentParentId}
                movePartCategoryIdFrom={props.movePartCategoryIdFrom}/>
            <PartCategoriesBodyTable items={props.items} />
            <ScrollLayer outerId={"#partCategories"} innerId={"#partCategories .panel"}/>
        </div>
    );
}

function PartCategoriesFloatLayer(props) {
    const isGoUp = props.parentId != 0;
    const isMovePartCategoryFrom = props.movePartCategoryIdFrom != null;
    const isFloatLayer = isGoUp || loginUserAdmin == true;

    return (
        <div className={'panel-heading' + (isFloatLayer ? '' : ' hide')} style={{position:'fixed'}}>
            <button name={'goUp'} className={'btn btn-primary' + (isGoUp ? '' : ' hide')} onClick={(e) => partCategories(props.parentParentId, e)}>&lt;</button>
            <button className={'btn btn-primary' + (loginUserAdmin == true ? '' : ' hide')} onClick={(e) => newPartCategoryModal(props.parentId, e)}>+</button>
            <button name={'moveHere'} className={'btn btn-primary' + (loginUserAdmin == true && isMovePartCategoryFrom ? '' : ' hide')} onClick={(e) => movePartCategoryHere(props.parentId, e)}>Paste</button>
            <button name={'moveHere'} className={'btn btn-danger' + (loginUserAdmin == true && isMovePartCategoryFrom ? '' : ' hide')} onClick={(e) => movePartCategoryCancel(e)}>Cancel</button>
        </div>
    );
}

function PartCategoriesBodyTable(props) {
    return (
        <div className={'panel-body'}>
        <table className="table table-bordered">
            <thead>
            <tr>
                {/*<th>ID</th>*/}
                {/*<th>P_ID</th>*/}
                <th>NAME(QTY/PARTS)</th>
                <th>REP_IMG</th>
            </tr>
            </thead>
            <tbody>
            {props.items.map(function(item, key) {
                var repImgs = [];
                if(typeof item.repImgs != "undefined") {
                    repImgs = JSON.parse(item.repImgs);
                }

                return <PartCategoriesElement key={key}
                    item={item}
                    movePartCategoryIdFrom={movePartCategoryIdFrom}
                    repImgs={repImgs}/>
            })}
            </tbody>
        </table>
        </div>
    );
}

function PartCategoriesElement(props) {
    const item = props.item;
    const repImgs = props.repImgs;

    const isMovePartCategoryIdFrom = props.movePartCategoryIdFrom != null;

    return (
        <tr>
            {/*<td>{item.blCategoryId}</td>*/}
            {/*<td>{item.parentId}</td>*/}
            <td>
                {item.setQty} / ({item.parts})
                {
                    item.blCategoryId == null ?
                        <button className={'btn btn-block btn-default'} onClick={(e) => partCategories(item.id, item.parentId, e)}>{item.name}</button> :
                        <button className={'btn btn-block btn-info'} onClick={(e) => partList(item.blCategoryId, item.parentId, e)}>{item.name}</button>
                }
                <button className={'btn btn-primary btn-sm btn-block' + (loginUserAdmin == true ? '' : ' hide')} onClick={(e) => movePartCategory(item.id, e)}>GoTo</button>
                {item.blCategoryId == null ? <button name={'moveHere'} className={'btn btn-primary btn-sm btn-block' + (loginUserAdmin == true && isMovePartCategoryIdFrom ? '' : ' hide')} onClick={(e) => movePartCategoryHere(item.id, e)}>Paste</button> : ''}
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


var movePartCategoryIdFrom = null;
function movePartCategory(categoryId, e) {
    if (typeof e != "undefined") e.preventDefault();

    movePartCategoryIdFrom = categoryId;
    $("[name=moveHere]").removeClass("hide");
    // alert("clipped!");
}

function movePartCategoryCancel(e) {
    if (typeof e != "undefined") e.preventDefault();

    movePartCategoryIdFrom = null;
    $("[name=moveHere]").addClass("hide");
}

function movePartCategoryHere(parentId, e) {
    if (typeof e != "undefined") e.preventDefault();
    // if(!confirm("여기로 카테고리를 이동하시겠습니까?")) return;

    $.ajax({
        url:"/admin/partCategory/move",
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
        $("[name=moveHere]").addClass("hide");
        partCategories($("#partCategoryForm [name=parentId]").val());
    });

}

