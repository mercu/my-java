// 부품 단건에 대해 보유 목록 리스팅하고(유사포함), 증감 메뉴 레이어 노출하기
function myPartWheresModal(partNo, colorId, setNo, matchId, partQty, e) {
    if (typeof e != "undefined") e.preventDefault();

    $('#myModal .modal-title').html("부품-단건 보유 목록 리스팅 및 증감")
    $('#myModal').modal('toggle');

    if (myPartWheresDOM == null) {
        ReactDOM.render(
            <MyPartWheresModalBody
                partNo={partNo}
                colorId={colorId}
                setNo={setNo}
                matchId={matchId}
                partQty={partQty}
            />
            , document.getElementById("myModal-body")
        );
    } else {
        myPartWheresDOM.setState({
            partNo : partNo,
            colorId : colorId,
            setNo : setNo,
            matchId : matchId,
            partQty : partQty
        });
        myPartWheresDOM.loadMyPartWheresSimilar(partNo, colorId, setNo);
    }
}

var myPartWheresDOM = null;
class MyPartWheresModalBody extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            partNo : props.partNo,
            colorId : props.colorId,
            setNo : props.setNo,
            matchId : props.matchId,
            partQty : props.partQty,
            myItemWheres : null
        };
        console.log(props);
    }

    setState(state) {
        super.setState(state);
        console.log(state);
    }

    componentDidMount() {
        myPartWheresDOM = this;
        this.loadMyPartWheresSimilar(this.state.partNo, this.state.colorId, this.state.setNo);
    }

    componentWillUnmount() {
        myPartWheresDOM = null;
    }

    loadMyPartWheresSimilar(partNo, colorId, setNo) {
        $.ajax({
            url:"/admin/myPartWheresSimilar",
            type : "GET",
            dataType : "json",
            data : {
                partNo : partNo,
                colorId : colorId,
                setNo : setNo
            },
            contentType: "application/json;charset=UTF-8",
            async : true
        }).done(function(data) {
            this.setState({
                myItemWheres : data
            });
        }.bind(this));
    }

    increaseMyPartWhereQty(partNo, colorId, whereCode, whereMore, val) {
        $.ajax({
            url:"/admin/myPartWhereIncrease",
            type : "POST",
            dataType : "json",
            data : {
                partNo : partNo,
                colorId : colorId,
                whereCode : whereCode,
                whereMore : whereMore,
                val : val,
                setNo : this.state.setNo,
                matchId : this.state.matchId
            },
            contentType: "application/x-www-form-urlencoded; charset=UTF-8",
            async : true
        }).done(function(data) {
            this.setState({
                myItemWheres : data
            });
        }.bind(this));
    }

    render() {
        var setNo = this.state.setNo;
        return (
            <div className={'panel panel-default'}>
                <div className={'panel-body'}>
                    partNo : {this.state.partNo}, colorId : {this.state.colorId}, setNo : {this.state.setNo}, partQty : {this.state.partQty}
                    <table className="table table-bordered">
                        <thead>
                        <tr>
                            <th>img</th>
                            <th>itemNo<br/>color</th>
                            <th>where</th>
                            <th>qty</th>
                            <th>set</th>
                        </tr>
                        </thead>
                        <tbody>
                        {this.state.myItemWheres != null && this.state.myItemWheres.map(function(whereInfo, key) {
                            var matchMyItemSetItemRatio = whereInfo.matchMyItemSetItemRatio;
                            var ratio = matchMyItemSetItemRatio && Math.round(matchMyItemSetItemRatio.matched / matchMyItemSetItemRatio.total * 100);
                            return <tr key={key}>
                                <td bgcolor={whereInfo.colorInfo != null && whereInfo.colorInfo.colorCode}>
                                    <img src={whereInfo.imgUrl} onError={(e)=>{e.target.onerror = null; whereInfo.partInfo != null ? e.target.src=whereInfo.partInfo.img : ''}}/>
                                </td>
                                <td>
                                    {whereInfo.itemNo}<br/>
                                    {whereInfo.colorInfo != null && whereInfo.colorInfo.name}({whereInfo.colorId})
                                </td>
                                <td bgcolor={(setNo == whereInfo.whereMore && 'f7d117') || ('storage' == whereInfo.whereCode && '2aabd2')}>
                                    {whereInfo.whereCode} - {whereInfo.whereMore}
                                    <br/>{matchMyItemSetItemRatio && (matchMyItemSetItemRatio.matched + '/' + matchMyItemSetItemRatio.total + '(' + ratio + '%)')}
                                </td>
                                <td>
                                    {whereInfo.qty}
                                </td>
                                <td>
                                    <button className={'btn btn-lg btn-primary'} onClick={(e) => increaseMyPartWhereQty(whereInfo.itemNo, whereInfo.colorId, whereInfo.whereCode, whereInfo.whereMore, e)}>+</button>&nbsp;&nbsp;
                                    <button className={'btn btn-lg btn-primary'} onClick={(e) => decreaseMyPartWhereQty(whereInfo.itemNo, whereInfo.colorId, whereInfo.whereCode, whereInfo.whereMore, e)}> - </button>&nbsp;&nbsp;
                                </td>
                            </tr>;
                        })}
                        </tbody>
                    </table>
                </div>
            </div>
        );
    }
}

function increaseMyPartWhereQty(partNo, colorId, whereCode, whereMore, e) {
    if (typeof e != "undefined") e.preventDefault();
    myPartWheresDOM.increaseMyPartWhereQty(partNo, colorId, whereCode, whereMore, 1);
}

function decreaseMyPartWhereQty(partNo, colorId, whereCode, whereMore, e) {
    if (typeof e != "undefined") e.preventDefault();
    myPartWheresDOM.increaseMyPartWhereQty(partNo, colorId, whereCode, whereMore, -1);
}


