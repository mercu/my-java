class PartList extends React.Component {
    constructor(props) {
        super(props);
    }

    componentDidMount() {
    }

    render() {
        var element = null;
        var categoryId = this.props.categoryId;
        var parentId = this.props.parentId;
        $.ajax({
            url:"/partList?categoryId=" + categoryId,
            type : "GET",
            dataType : "json",
            ContentType: "application/json",
            async : false
        }).done(function(data){
            element =
                <div className={'panel panel-default'}>
                    <div className={'panel-heading'} style={{position:'fixed'}}>
                        <p>
                            <button className={'btn btn-primary'} onClick={(e) => partCategories(parentId, e)}>상위</button>
                        </p>
                    </div>
                    <div className={'panel-body'}>
                        <table className="table table-bordered">
                            <thead>
                            <tr>
                                <th>img<br/>partNo</th>
                                <th>setQty<br/>myQty</th>
                                <th>partName</th>
                            </tr>
                            </thead>
                            <tbody>
                            {data.map(function(item, key) {
                                return <tr key={key}>
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
                                        {item.myItemGroups.length > 0 ? <MyItems myItemGroups={item.myItemGroups}/> : ""}
                                    </td>
                                </tr>
                            })}
                            </tbody>
                        </table>
                    </div>
                </div>
        });
        return element;
    }

}

function MyItems(props) {
    var myItemGroups = props.myItemGroups;
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
                return (
                    <tr key={key}>
                        <td><img src={item.repImg}/></td>
                        <td>{item.qty}</td>
                        <td>
                            <ul>
                            {item.myItems.map(function(item, key) {
                                return (
                                    <li>({item.qty}) {item.whereCode}-{item.whereMore}</li>
                                );
                            })}
                            </ul>
                        </td>
                    </tr>
                );
            })}
            </tbody>
        </table>
    );
}

function partList(categoryId, parentId, e) {
    if (typeof e != "undefined") e.preventDefault();

    ReactDOM.render(
        <PartList categoryId={categoryId} parentId={parentId}/>
        , document.getElementById("partCategories")
    );
    $("#partCategories").removeClass("hide");
}
