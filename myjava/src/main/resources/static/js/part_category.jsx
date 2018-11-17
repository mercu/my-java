function partCategories(parentId, parentParentId, e) {
    if (typeof parentId == "undefined") parentId = 0;
    if (typeof parentParentId == "undefined" || typeof parentParentId == "object") parentParentId = 0;
    if (typeof e != "undefined") e.preventDefault();

    if (partCategoriesDOM == null) {
        ReactDOM.render(
            <PartCategories
                parentId={parentId}
                parentParentId={parentParentId}
            />
            , document.getElementById("partCategories")
        );
        partCategoriesAjax(parentId);
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

var partCategoriesDOM = null;
class PartCategories extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            parentId : props.parentId,
            parentParentId : props.parentParentId,
            movePartCategoryIdFrom : props.movePartCategoryIdFrom,
            loginUserAdmin : navigatorDOM.state.loginUserAdmin,
            categoryManageEnable : false,
            items : []
        };
    }

    setState(state) {
        super.setState(state);
    }

    componentDidMount() {
        partCategoriesDOM = this;
        // partCategoriesAjax(this.state.parentId);
    }

    componentWillUnmount() {
        partCategoriesDOM = null;
    }

    render() {
        return (
            <PartCategoriesRoot/>
        );
    }
}

function PartCategoriesRoot() {
    if (partCategoriesDOM == null) return '';

    return (
        <div className={'panel panel-default'}>
            <PartCategoriesFloatLayer
                parentId={partCategoriesDOM.state.parentId}
                parentParentId={partCategoriesDOM.state.parentParentId}
                categoryManageEnable={partCategoriesDOM.state.categoryManageEnable}
                movePartCategoryIdFrom={partCategoriesDOM.state.movePartCategoryIdFrom} />
            <PartCategoriesBodyTable items={partCategoriesDOM.state.items} />
            <ScrollLayer outerId={"#partCategories"} innerId={"#partCategories .panel"}/>
        </div>
    );
}

function PartCategoriesFloatLayer() {
    if (partCategoriesDOM == null) return '';

    const isGoUp = partCategoriesDOM.state.parentId != 0;
    const isFloatLayer = isGoUp || loginUserAdmin == true;

    return (
        <div className={'panel-heading' + (isFloatLayer ? '' : ' hide')} style={{position:'fixed'}}>
            <button name={'goUp'} className={'btn btn-primary' + (isGoUp ? '' : ' hide')} onClick={(e) => partCategories(partCategoriesDOM.state.parentParentId, e)}>&lt;</button>
            {/* 카테고리 관리 기능 (어드민용) */}
            <CategoryManageFloatLayer
                parentId={partCategoriesDOM.state.parentId}
                categoryManageEnable={partCategoriesDOM.state.categoryManageEnable}
                movePartCategoryIdFrom={partCategoriesDOM.state.movePartCategoryIdFrom}
            />
        </div>
    );
}

/**
 * 카테고리 관리 기능 (어드민용)
 */
function CategoryManageFloatLayer() {
    if (partCategoriesDOM == null) return '';

    if (loginUserAdmin == false) return '';
    if (partCategoriesDOM.state.categoryManageEnable) {
        return ([
            <button className={'btn btn-primary'} onClick={(e) => newPartCategoryModal(partCategoriesDOM.state.parentId, e)}>+</button>,
            <button name={'moveHere'} className={'btn btn-primary' + (partCategoriesDOM.state.movePartCategoryIdFrom != null ? '' : ' hide')} onClick={(e) => movePartCategoryHere(partCategoriesDOM.state.parentId, e)}>Paste</button>,
            <button name={'moveHere'} className={'btn btn-danger' + (partCategoriesDOM.state.movePartCategoryIdFrom != null ? '' : ' hide')} onClick={(e) => movePartCategoryCancel(e)}>Cancel</button>,
            <button className={'btn btn-default'} onClick={(e) => disableCategoryManage(e)}>CATE</button>
        ]);
    } else {
        return <button className={'btn btn-primary'} onClick={(e) => enableCategoryManage(e)}>CATE</button>;
    }
}

function enableCategoryManage(e) {
    if (typeof e != "undefined") e.preventDefault();
    if (partCategoriesDOM == null) return;

    partCategoriesDOM.setState({categoryManageEnable : true});
}

function disableCategoryManage(e) {
    if (typeof e != "undefined") e.preventDefault();
    if (partCategoriesDOM == null) return;

    partCategoriesDOM.setState({categoryManageEnable : false});
}

function PartCategoriesBodyTable() {
    if (partCategoriesDOM == null) return '';

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
            {partCategoriesDOM.state.items.map(function(item, key) {
                var repImgs = [];
                if(typeof item.repImgs != "undefined") {
                    repImgs = JSON.parse(item.repImgs);
                }

                return <PartCategoriesElement key={key}
                    item={item}
                    repImgs={repImgs}
                />
            })}
            </tbody>
        </table>
        </div>
    );
}

function PartCategoriesElement(props) {
    if (partCategoriesDOM == null) return '';

    const item = props.item;
    const repImgs = props.repImgs;

    const isMovePartCategoryIdFrom = partCategoriesDOM.state.movePartCategoryIdFrom != null;

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
                <button className={'btn btn-primary btn-sm btn-block' + (partCategoriesDOM.state.categoryManageEnable ? '' : ' hide')} onClick={(e) => movePartCategory(item.id, e)}>GoTo</button>
                {item.blCategoryId == null ? <button name={'moveHere'} className={'btn btn-primary btn-sm btn-block' + (partCategoriesDOM.state.categoryManageEnable == true && isMovePartCategoryIdFrom ? '' : ' hide')} onClick={(e) => movePartCategoryHere(item.id, e)}>Paste</button> : ''}
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


function movePartCategory(categoryId, e) {
    if (typeof e != "undefined") e.preventDefault();

    partCategoriesDOM.state.movePartCategoryIdFrom = categoryId;
    $("[name=moveHere]").removeClass("hide");
    // alert("clipped!");
}

function movePartCategoryCancel(e) {
    if (typeof e != "undefined") e.preventDefault();

    partCategoriesDOM.state.movePartCategoryIdFrom = null;
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
            "categoryIdFrom" : partCategoriesDOM.state.movePartCategoryIdFrom,
            "parentIdTo" : parentId
        },
        ContentType: "application/json",
        async : true
    }).always(function(data) {
        // alert(data.responseText);
        partCategoriesDOM.setState({movePartCategoryIdFrom : null});
        $("[name=moveHere]").addClass("hide");
        partCategories($("#partCategoryForm [name=parentId]").val());
    });

}

