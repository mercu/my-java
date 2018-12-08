var matchSetPartsDOM = null;
function matchSetParts(setId, e) {
    if (typeof e != "undefined") e.preventDefault();

    if (matchSetPartsDOM == null) {
        ReactDOM.render(
            <MatchSetParts setId={setId}/>
            , document.getElementById("candidate")
        );
    } else {
        matchSetPartsAjax(setId);
    }
    $("#candidate").removeClass("hide");

}

class MatchSetParts extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            setId : props.setId,
            items : []
        };
    }

    setState(state) {
        super.setState(state);
    }

    componentDidMount() {
        matchSetPartsDOM = this;
        matchSetPartsAjax(this.state.setId);
    }

    componentWillUnmount() {
        matchSetPartsDOM = null;
    }

    render() {
        return (
            <MatchSetPartsRoot
                items={this.state.items}
            />
        );
    }
}

function MatchSetPartsRoot(props) {
    return (
        <div className={'panel panel-default'}>
            <div className={'panel-body'}>
                <table className="table table-bordered">
                    <thead>
                    <tr>
                        <th>item</th>
                    </tr>
                    </thead>
                    <tbody>
                    {props.items.map(function(item, key) {
                        return <tr key={key}>
                            <td>{item.setId}</td>
                        </tr>;
                    })}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

function matchSetPartsAjax(setId) {
    $.ajax({
        url:"/admin/matchSetParts",
        type : "GET",
        dataType : "json",
        data : {setId : setId},
        contentType: "application/json;charset=UTF-8",
        async : true
    }).done(function(data) {
        matchSetPartsDOM.setState({
            items : data
        });
    });
}

