var myPartsDOM = null;
function myParts(e) {
    if (typeof e != "undefined") e.preventDefault();

    if (myPartsDOM == null) {
        ReactDOM.render(
            <MyParts/>
            , document.getElementById("myParts")
        );
    } else {
        myPartsAjax();
    }
    $("#myParts").removeClass("hide");

}

class MyParts extends React.Component {
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
        myPartsDOM = this;
        myPartsAjax(this.state.parentId);
    }

    componentWillUnmount() {
        myPartsDOM = null;
    }

    render() {
        return (
            <MyPartsRoot
                items={this.state.items}
            />
        );
    }
}

function MyPartsRoot(props) {
    return (
        <div className={'panel panel-default'}>
            <div className={'panel-body'}>
                <table className="table table-bordered">
                    <thead>
                    <tr>
                        <th rowSpan={2}>img</th>
                        <th rowSpan={2}>itemNo</th>
                        <th rowSpan={2}>totalQty</th>
                        <th colSpan={3}>subItems</th>
                    </tr>
                    <tr>
                        <th>whereCode</th>
                        <th>whereMore</th>
                        <th>qty</th>
                    </tr>
                    </thead>
                    <tbody>
                    {props.items.map(function(item, key) {
                        return <tr key={key}>
                            <td bgcolor={item.colorCode}><img src={item.repImg} alt={item.repImgOriginal} onError={(e)=>{e.target.onerror = null; e.target.src=item.repImgOriginal}}/></td>
                            <td>{item.itemNo}</td>
                            <td>{item.qty}</td>
                            <td colSpan={3}><SubItems subItem={item.myItems}/></td>
                        </tr>;
                    })}
                    </tbody>
                </table>
            </div>
            <ScrollLayer outerId={"#myParts"} innerId={"#myParts .panel"} />
        </div>
    );
}

function SubItems(props) {
    const subItems = props.subItem;
    return (
        <table className="table table-bordered" style={{marginBottom:"0"}}>
            <tbody>
            {subItems.map(function(item, key) {
                return <tr key={key}>
                    <td>{item.whereCode}</td>
                    <td>{item.whereMore}</td>
                    <td>{item.qty}</td>
                </tr>;
            })}
            </tbody>
        </table>
    );
}

function myPartsAjax() {
    $.ajax({
        url:"/admin/myPartsByGroup",
        type : "GET",
        dataType : "json",
        data : {},
        contentType: "application/json;charset=UTF-8",
        async : true
    }).done(function(data) {
        myPartsDOM.setState({
            items : data
        });
    });
}

