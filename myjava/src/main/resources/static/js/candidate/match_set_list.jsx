var matchSetListDOM = null;
function matchSetList(matchId, e) {
    if (typeof e != "undefined") e.preventDefault();

    if (matchSetListDOM == null) {
        ReactDOM.render(
            <MatchSetList matchId={matchId}/>
            , document.getElementById("candidate")
        );
    } else {
        matchSetListAjax(matchId);
    }
    $("#candidate").removeClass("hide");

}

class MatchSetList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            matchId : props.matchId,
            items : []
        };
    }

    setState(state) {
        super.setState(state);
    }

    componentDidMount() {
        matchSetListDOM = this;
        matchSetListAjax(this.state.matchId);
    }

    componentWillUnmount() {
        matchSetListDOM = null;
    }

    render() {
        return (
            <MatchSetListRoot
                items={this.state.items}
            />
        );
    }
}

function MatchSetListRoot(props) {
    return (
        <div className={'panel panel-default'}>
            <div className={'panel-body'}>
                <table className="table table-bordered">
                    <thead>
                    <tr>
                        <th>image</th>
                        <th>setNo</th>
                        <th>matched</th>
                        <th>ratio</th>
                    </tr>
                    </thead>
                    <tbody>
                    {props.items.map(function(item, key) {
                        return <tr key={key}>
                            <td>
                                <img src={'https://img.bricklink.com/ItemImage/ST/0/' + item.setNo + '-1.t1.png'} alt={item.setNo}/>
                            </td>
                            <td>
                                <button className={'btn btn-default btn-block'} onClick={(e) => matchSetParts(item.matchId, item.setId, e)}>{item.setNo}</button>
                            </td>
                            <td>{item.matched} / {item.total}</td>
                            <td>{item.ratio}</td>
                        </tr>;
                    })}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

function matchSetListAjax(matchId) {
    $.ajax({
        url:"/admin/matchSetList",
        type : "GET",
        dataType : "json",
        data : {matchId : matchId},
        contentType: "application/json;charset=UTF-8",
        async : true
    }).done(function(data) {
        matchSetListDOM.setState({
            items : data
        });
    });
}

