var myWheresDOM = null;
function myWheres(e) {
    if (typeof e != "undefined") e.preventDefault();

    if (myWheresDOM == null) {
        ReactDOM.render(
            <MyWheres/>
            , document.getElementById("myWheres")
        );
    } else {
        myWheresAjax();
    }
    $("#myWheres").removeClass("hide");

}

class MyWheres extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            items : []
        };
    }

    setState(state) {
        super.setState(state);
    }

    componentDidMount() {
        myWheresDOM = this;
        myWheresAjax(this.state.parentId);
    }

    componentWillUnmount() {
        myWheresDOM = null;
    }

    render() {
        return (
            <MyWheresRoot
                items={this.state.items}
            />
        );
    }
}

function MyWheresRoot(props) {
    return (
        <div className={'panel panel-default'}>
            <div className={'panel-body'}>
                <table className="table table-bordered">
                    <thead>
                    <tr>
                        <th>no</th>
                        <th>img</th>
                        <th>where</th>
                        <th>totalQty</th>
                        <th>func</th>
                    </tr>
                    </thead>
                    <tbody>
                    {props.items.map(function(item, key) {
                        return <tr key={key}>
                            <td>{key}</td>
                            <td>
                                <a href={item.linkUrl} target={'_blank'}>
                                    <img src={item.imgUrl}/>
                                </a>
                            </td>
                            <td>{item.whereCode}-{item.whereMore}</td>
                            <td>{item.qty}</td>
                            <td>
                                <button className={'btn btn-lg btn-danger'} onClick={(e) => increaseMyPartWhereQty(whereInfo.itemNo, whereInfo.colorId, whereInfo.whereCode, whereInfo.whereMore, e)}>LOCK</button>&nbsp;&nbsp;
                                <button className={'btn btn-lg btn-info'} onClick={(e) => decreaseMyPartWhereQty(whereInfo.itemNo, whereInfo.colorId, whereInfo.whereCode, whereInfo.whereMore, e)}>UNLOCK</button>&nbsp;&nbsp;
                            </td>
                        </tr>;
                    })}
                    </tbody>
                </table>
            </div>
            <ScrollLayer outerId={"#myWheres"} innerId={"#myWheres .panel"} />
        </div>
    );
}

function myWheresAjax() {
    $.ajax({
        url:"/admin/groupByWheres",
        type : "GET",
        dataType : "json",
        data : {},
        contentType: "application/json;charset=UTF-8",
        async : true
    }).done(function(data) {
        myWheresDOM.setState({
            items : data
        });
    });
}

