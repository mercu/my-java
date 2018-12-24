/**
 * 부품 카테고리 목록
 */
function partCategories(parentId, e) {
    if (typeof parentId == "undefined") parentId = 0;
    if (typeof e != "undefined") e.preventDefault();

    if (partCategoriesDOM == null) {
        ReactDOM.render(
            <PartCategories parentId={parentId} />
            , document.getElementById("partCategories")
        );
    } else {
        partCategoriesDOM.loadPartCategories(parentId);
    }
    $("#partList").addClass("hide");
    $("#partCategories").removeClass("hide");
}


/**
 * PartCategories React Component
 */
var partCategoriesDOM = null;
class PartCategories extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            parentId : props.parentId,
            upParentId : 0,
            movePartCategoryIdFrom : null,
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
        this.loadPartCategories(this.state.parentId);
    }

    componentWillUnmount() {
        partCategoriesDOM = null;
    }

    loadPartCategories(parentId) {
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
            this.setState({
                parentId : data.parentCategory.id,
                upParentId : data.parentCategory.parentId,
                items : data.partCategories
            });
        }.bind(this));
    }

    render() {
        return (
            <div className={'panel panel-default'}>
                upParentId : {this.state.upParentId}, parentId : {this.state.parentId}
                <PartCategoriesFloatMenuLayer
                    parentId={this.state.parentId}
                    upParentId={this.state.upParentId}
                    categoryManageEnable={this.state.categoryManageEnable}
                    movePartCategoryIdFrom={this.state.movePartCategoryIdFrom} />
                <PartCategoriesBodyTable items={this.state.items} />
                <ScrollLayer outerId={"#partCategories"} innerId={"#partCategories .panel"}/>
            </div>
        );
    }
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
                        <button className={'btn btn-block btn-default'} onClick={(e) => partCategories(item.id, e)}>{item.name}</button> :
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




/**
 * 카테고리 플로팅 메뉴
 */
function PartCategoriesFloatMenuLayer(props) {
    const isGoUp = props.parentId != 0;
    const isFloatLayer = isGoUp || navigatorDOM.state.loginUserAdmin == true;

    return (
        <div className={'panel-heading ' + (isFloatLayer ? '' : 'hide')} style={{position:'fixed', margin:'20px', top:'200px'}}>
            <button name={'goUp'} className={'btn btn-primary' + (isGoUp ? '' : ' hide')} onClick={(e) => partCategories(props.upParentId, e)}>상위</button>
            {/* 카테고리 관리 기능 (어드민용) */}
            <CategoryManageFloatMenus
                parentId={props.parentId}
                categoryManageEnable={props.categoryManageEnable}
                movePartCategoryIdFrom={props.movePartCategoryIdFrom}
            />
        </div>
    );

}

/**
 * 카테고리 관리 기능 (어드민용)
 */
function CategoryManageFloatMenus(props) {
    if (navigatorDOM.state.loginUserAdmin == false) return '';
    if (props.categoryManageEnable) {
        return ([
            <button key={'CategoryManageFloatMenus_1'} className={'btn btn-info'} onClick={(e) => disableCategoryManage(e)}>::</button>,
            <button key={'CategoryManageFloatMenus_2'} className={'btn btn-primary'} onClick={(e) => addMyPartInCategoryModal(props.parentId, null, e)}>P[+]</button>,
            <button key={'CategoryManageFloatMenus_3'} className={'btn btn-primary'} onClick={(e) => newPartCategoryModal(props.parentId, e)}>C[+]</button>,
            <button key={'CategoryManageFloatMenus_4'} name={'moveHere'} className={'btn btn-primary' + (props.movePartCategoryIdFrom != null ? '' : ' hide')} onClick={(e) => movePartCategoryHere(props.parentId, e)}>Paste</button>,
            <button key={'CategoryManageFloatMenus_5'} name={'moveHere'} className={'btn btn-danger' + (props.movePartCategoryIdFrom != null ? '' : ' hide')} onClick={(e) => movePartCategoryCancel(e)}>Cancel</button>
        ]);
    } else {
        return <button className={'btn btn-primary'} onClick={(e) => enableCategoryManage(e)}>::</button>;
    }
}

function enableCategoryManage(e) {
    if (typeof e != "undefined") e.preventDefault();
    partCategoriesDOM.setState({categoryManageEnable : true});
}

function disableCategoryManage(e) {
    if (typeof e != "undefined") e.preventDefault();
    partCategoriesDOM.setState({categoryManageEnable : false});
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

