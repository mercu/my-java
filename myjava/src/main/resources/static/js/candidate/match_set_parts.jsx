var matchSetPartsDOM = null;
function matchSetParts(matchId, setId, e) {
    if (typeof e != "undefined") e.preventDefault();

    if (matchSetPartsDOM == null) {
        ReactDOM.render(
            <MatchSetParts
                matchId={matchId}
                setId={setId}/>
            , document.getElementById("candidate")
        );
    } else {
        matchSetPartsAjax(matchId, setId);
    }
    $("#candidate").removeClass("hide");

}

class MatchSetParts extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            matchId : props.matchId,
            setId : props.setId,
            setInfo : null,
            items : []
        };
    }

    setState(state) {
        super.setState(state);
    }

    componentDidMount() {
        matchSetPartsDOM = this;
        matchSetPartsAjax(this.state.matchId, this.state.setId);
    }

    componentWillUnmount() {
        matchSetPartsDOM = null;
    }

    render() {
        return (
            <MatchSetPartsRoot
                setInfo={this.state.setInfo}
                items={this.state.items}
            />
        );
    }
}

function MatchSetPartsRoot(props) {
    var setNo = props.setInfo != null && props.setInfo.setNo;
    return (
        <div className={'panel panel-default'}>
            <div className={'panel-body'}>
                setNo : {setNo}
                <table className="table table-bordered">
                    <thead>
                    <tr>
                        <th>img</th>
                        <th>itemNo<br/>partName</th>
                        <th>qty</th>
                        <th>where</th>
                    </tr>
                    </thead>
                    <tbody>
                    {props.items.map(function(item, key) {
                        return <tr key={key}>
                            <td bgcolor={item.colorInfo != null ? item.colorInfo.colorCode : ''}>
                                <img src={item.imgUrl} onError={(e)=>{e.target.onerror = null; item.partInfo != null ? e.target.src=item.partInfo.img : ''}}/>
                            </td>
                            <td>
                                <a href={'https://www.bricklink.com/v2/catalog/catalogitem.page?id=' + (item.partInfo != null ? item.partInfo.id : '') + '#T=C'} target={'_blank'}>{item.itemNo}</a>
                                <br/>
                                {item.partInfo != null ? item.partInfo.partName : ''}
                            </td>
                            <td>{item.qty} / {item.partQty}</td>
                            <td>
                                {item.matched && 'matched'}

                                <MyItemsWhere
                                    setNo={setNo}
                                    myItems={item.myItems}
                                    matched={item.matched}
                                />
                            </td>
                        </tr>;
                    })}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

function MyItemsWhere(props) {
    if (props.matched) return '';

    var setNo = props.setNo;
    return (
        <table className="table table-bordered">
            <thead>
            <tr>
                <th>where</th>
                <th>qty</th>
                <th></th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            {props.myItems.map(function(item, key) {
                return <tr key={key}>
                    <td>{item.whereCode}-{item.whereMore}</td>
                    <td>
                        {item.qty}
                    </td>
                    <td>
                        <button className={'btn btn-block btn-info'} >+</button>
                    </td>
                    <td>
                        <button className={'btn btn-block btn-info'} >-</button>
                    </td>
                </tr>;
            })}
            </tbody>
        </table>
    );
}

function matchSetPartsAjax(matchId, setId) {
    $.ajax({
        url:"/admin/matchSetParts",
        type : "GET",
        dataType : "json",
        data : {
            matchId : matchId,
            setId : setId
        },
        contentType: "application/json;charset=UTF-8",
        async : true
    }).done(function(data) {
        matchSetPartsDOM.setState({
            setInfo : data.setInfo,
            items : data.matchItems
        });
    });
}

