var partListDOM = null;

function partList(blCategoryId, parentId, e) {
    if (typeof parentId == "undefined") parentId = 0;
    if (typeof e != "undefined") e.preventDefault();

    if (partListDOM == null) {
        ReactDOM.render(
            <PartList blCategoryId={blCategoryId} parentId={parentId}/>
            , document.getElementById("partList")
        );
    } else {
        partListAjax(blCategoryId, parentId);
    }
    $("#partCategories").addClass("hide");
    $("#partList").removeClass("hide");
}

function partListAjax(blCategoryId, parentId) {
    $.ajax({
        url:"/partList",
        type : "GET",
        dataType : "json",
        data : {blCategoryId : blCategoryId},
        ContentType: "application/json;charset=UTF-8",
        async : true
    }).done(function(data){
        partListDOM.setState({
            items : data,
            blCategoryId : blCategoryId,
            parentId: parentId
        });
    });
}


class PartList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            blCategoryId : props.blCategoryId,
            parentId : props.parentId,
            partManageEnable : false,
            items : []
        };
    }

    setState(state) {
        super.setState(state);
    }

    componentDidMount() {
        partListDOM = this;
        partListAjax(this.state.blCategoryId);
    }

    componentWillUnmount() {
        partListDOM = null;
    }

    render() {
        return (
            <div className={'panel panel-default'}>
                parentId : {this.state.parentId}, blCategoryId : {this.state.blCategoryId}
                <PartsFloatMenuLayer
                    blCategoryId={this.state.blCategoryId}
                    parentId={this.state.parentId}
                    partManageEnable={this.state.partManageEnable} />
                <div className={'panel-body'}>
                    <PartInfosBody items={this.state.items} />
                </div>
                <ScrollLayer outerId={"#partList"} innerId={"#partList .panel"} />
            </div>
        );
    }

}

/**
 * 카테고리 플로팅 메뉴
 */
function PartsFloatMenuLayer(props) {
    return (
        <div className={'panel-heading'} style={{position:'fixed', margin:'20px', top:'200px'}}>
            <button name={'goUp'} className={'btn btn-primary'} onClick={(e) => partCategories(props.parentId, e)}>상위</button>
            {/* 카테고리 관리 기능 (어드민용) */}
            <PartManageFloatMenus
                blCategoryId={props.blCategoryId}
                partManageEnable={props.partManageEnable}
            />
        </div>
    );

}

/**
 * 부품 관리 기능 (어드민용)
 */
function PartManageFloatMenus(props) {
    if (navigatorDOM.state.loginUserAdmin == false) return '';
    if (props.partManageEnable) {
        return <button key={'PartManageFloatMenus_1'} className={'btn btn-info'} onClick={(e) => disablePartManage(e)}>::</button>;
        {/*<button key={'PartManageFloatMenus_2'} className={'btn btn-primary'} onClick={(e) => newMyPartModal(props.blCategoryId, null, e)}>P[+]</button>*/}
    } else {
        return <button className={'btn btn-primary'} onClick={(e) => enablePartManage(e)}>::</button>;
    }
}

function enablePartManage(e) {
    if (typeof e != "undefined") e.preventDefault();
    partListDOM.setState({partManageEnable : true});
}

function disablePartManage(e) {
    if (typeof e != "undefined") e.preventDefault();
    partListDOM.setState({partManageEnable : false});
}



function PartInfosBody(props) {
    const items = props.items;

    return (
        <table className="table table-bordered">
            <thead>
            <tr>
                <th>img<br/>partNo</th>
                <th>info</th>
                <th>partName</th>
            </tr>
            </thead>
            <tbody>
            {items.map(function(item, key) {
                return <PartInfo
                    key={key}
                    item={item}
                />;
            })}
            </tbody>
        </table>
    );
}

function PartInfo(props) {
    const item = props.item;

    return (
        <tr>
            <td>
                <img src={item.img}/><br/>
                <a href={'https://www.bricklink.com/v2/catalog/catalogitem.page?id=' + item.id + '#T=C'} target={'_blank'}>{item.partNo}</a>
            </td>
            <td>
                itemId : {item.id}<br/>
                setQty : {item.setQty}<br/>
                myItemsQty : {item.myItemsQty}<br/>
                <button className={'btn btn-primary btn-sm btn-block' + (partListDOM.state.partManageEnable ? '' : ' hide')} onClick={(e) => newMyPartModal(item.categoryId, item.partNo, e)}>P[+]</button>
            </td>
            <td>
                {item.partName}<br/>
                {item.myItemGroups != null && item.myItemGroups.length > 0 ? <MyItemGroups myItemGroups={item.myItemGroups}/> : ""}
            </td>
        </tr>
    );
}

function MyItemGroups(props) {
    const myItemGroups = props.myItemGroups;

    return (
        <div style={{maxHeight:'200px', overflowY:'scroll'}}>
        <table className="table table-bordered">
            <thead>
            <tr>
                <th>colorId</th>
                <th>qty</th>
                <th>where</th>
            </tr>
            </thead>
            <tbody>
            {myItemGroups.map(function(item, key) {
                return <MyItemGroup item={item} key={key}/>;
            })}
            </tbody>
        </table>
        </div>
    );
}

function MyItemGroup(props) {
    const item = props.item;

    return (
        <tr>
            <td><img src={item.repImg}/></td>
            <td>{item.qty}</td>
            <td>
                <ul>
                    <MyItems myItems={item.myItems}/>
                </ul>
            </td>
        </tr>
    );
}

function MyItems(props) {
    const myItems = props.myItems;
    return (
        myItems.map(function(item, key) {
            return <MyItem item={item} key={key}/>;
        })
    );
}

function MyItem(props) {
    const item = props.item;
    return (
        <li>({item.qty}) {item.whereCode}-{item.whereMore}</li>
    );
}

