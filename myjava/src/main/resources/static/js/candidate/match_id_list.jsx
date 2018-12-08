var matchIdListDOM = null;
function matchIdList(e) {
    if (typeof e != "undefined") e.preventDefault();

    if (matchIdListDOM == null) {
        ReactDOM.render(
            <MatchIdList/>
            , document.getElementById("candidate")
        );
    } else {
        matchIdListAjax();
    }
    $("#candidate").removeClass("hide");

}

class MatchIdList extends React.Component {
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
        matchIdListDOM = this;
        matchIdListAjax();
    }

    componentWillUnmount() {
        matchIdListDOM = null;
    }

    render() {
        return (
            <MatchIdListRoot
                items={this.state.items}
            />
        );
    }
}

function MatchIdListRoot(props) {
    return (
        <div className={'panel panel-default'}>
            <div className={'panel-body'}>
                <table className="table table-bordered">
                    <thead>
                    <tr>
                        <th>matchId</th>
                    </tr>
                    </thead>
                    <tbody>
                    {props.items.map(function(item, key) {
                        return <tr key={key}>
                            <td>
                                <button className={'btn btn-default btn-block'} onClick={(e) => matchSetList(item, e)}>{item}</button>
                            </td>
                        </tr>;
                    })}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

function matchIdListAjax() {
    $.ajax({
        url:"/admin/matchIdList",
        type : "GET",
        dataType : "json",
        data : {},
        contentType: "application/json;charset=UTF-8",
        async : true
    }).done(function(data) {
        matchIdListDOM.setState({
            items : data
        });
    });
}

