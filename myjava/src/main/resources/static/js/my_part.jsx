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
                        <th>img</th>
                        <th>itemNo</th>
                        <th>colorId</th>
                        <th>whereCode</th>
                        <th>whereMore</th>
                        <th>qty</th>
                    </tr>
                    </thead>
                    <tbody>
                    {props.items.map(function(item, key) {
                        return <tr key={key}>
                            <td><img src={item.partInfo.img}/></td>
                            <td>{item.itemNo}</td>
                            <td>{item.colorId}</td>
                            <td>{item.whereCode}</td>
                            <td>{item.whereMore}</td>
                            <td>{item.qty}</td>
                        </tr>;
                    })}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

function myPartsAjax() {
    $.ajax({
        url:"/admin/myParts",
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

