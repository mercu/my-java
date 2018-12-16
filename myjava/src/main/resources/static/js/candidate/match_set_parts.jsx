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
                matchId={this.state.matchId}
            />
        );
    }
}

function MatchSetPartsRoot(props) {
    var setNo = props.setInfo != null && props.setInfo.setNo;
    return (
        <div className={'panel panel-default'}>
            <MatchSetPartsFloatMenuLayer
                matchId={props.matchId} />
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
                            <td bgcolor={!item.matched && 'f7d117'}>{item.qty} / {item.partQty}</td>
                            <td bgcolor={!item.matched && 'f7d117'}>
                                {/* 부품 단건에 대해 보유 목록 리스팅하고(유사포함), 증감 메뉴 레이어 노출하기 */}
                                <button name={'myItemManipulate'} className={'btn btn-primary'} onClick={(e) => myPartWheresModal(item.itemNo, item.colorId, setNo, e)}>조회/증감</button>

                                {/*<MyItemsWhere*/}
                                    {/*setNo={setNo}*/}
                                    {/*myItems={item.myItems}*/}
                                    {/*matched={item.matched}*/}
                                {/*/>*/}
                            </td>
                        </tr>;
                    })}
                    </tbody>
                </table>
            </div>
            <ScrollLayer outerId={"#candidate"} innerId={"#candidate .panel"} />
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

/**
 * 플로팅 메뉴
 */
function MatchSetPartsFloatMenuLayer(props) {
    return (
        <div className={'panel-heading'} style={{position:'fixed', margin:'20px', top:'200px'}}>
            <button name={'goUp'} className={'btn btn-primary'} onClick={(e) => matchSetList(props.matchId, e)}>상위</button>
        </div>
    );

}

