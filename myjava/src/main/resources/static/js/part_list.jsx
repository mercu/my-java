var partListDOM = null;

function partList(categoryId, parentId, e) {
    if (typeof parentId == "undefined") parentId = 0;
    if (typeof e != "undefined") e.preventDefault();

    if (partListDOM == null) {
        ReactDOM.render(
            <PartList categoryId={categoryId} parentId={parentId}/>
            , document.getElementById("partList")
        );
    } else {
        partListAjax(categoryId, parentId);
    }
    $("#partCategories").addClass("hide");
    $("#partList").removeClass("hide");
}

function partListAjax(categoryId, parentId) {
    $.ajax({
        url:"/partList",
        type : "GET",
        dataType : "json",
        data : {categoryId : categoryId},
        ContentType: "application/json;charset=UTF-8",
        async : true
    }).done(function(data){
        partListDOM.setState({
            items : data,
            parentId: parentId
        });
    });
}


class PartList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            categoryId : props.categoryId,
            parentId : props.parentId,
            items : []
        };
    }

    setState(state) {
        super.setState(state);
    }

    componentDidMount() {
        partListDOM = this;
        partListAjax(this.state.categoryId);
    }

    componentWillUnmount() {
        partListDOM = null;
    }

    render() {
        return (
            <PartListRoot
                parentId={this.state.parentId}
                items={this.state.items}/>
        );
    }

}

function PartListRoot(props) {
    const items = props.items;
    const parentId = props.parentId;

    return (
        <div className={'panel panel-default'}>
            <div className={'panel-heading'} style={{position:'fixed'}}>
                <button className={'btn btn-primary'} onClick={(e) => partCategories(parentId, e)}>상위</button>
            </div>
            <div className={'panel-body'}>
                <PartInfosBody items={items}/>
            </div>
            <ScrollLayer outerId={"#partList"} innerId={"#partList .panel"} />
        </div>
    );
}

function PartInfosBody(props) {
    const items = props.items;

    return (
        <table className="table table-bordered">
            <thead>
            <tr>
                <th>img<br/>partNo</th>
                <th>setQty<br/>myQty</th>
                <th>partName</th>
            </tr>
            </thead>
            <tbody>
            {items.map(function(item, key) {
                return <PartInfo item={item} key={key}/>;
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
                {item.setQty}
                <br/>/<br/>
                {item.myItemsQty}
            </td>
            <td>
                {item.partName}<br/>
                {item.myItemGroups.length > 0 ? <MyItemGroups myItemGroups={item.myItemGroups}/> : ""}
            </td>
        </tr>
    );
}

function MyItemGroups(props) {
    const myItemGroups = props.myItemGroups;

    return (
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

