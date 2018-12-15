// 부품 단건에 대해 보유 목록 리스팅하고(유사포함), 증감 메뉴 레이어 노출하기
function myPartWheresModal(partNo, colorId, setNo, e) {
    if (typeof e != "undefined") e.preventDefault();
    console.log("setNo : " + setNo);
    console.log(setNo);

    $('#myModal .modal-title').html("부품-단건 보유 목록 리스팅 및 증감")
    $('#myModal').modal('toggle');

    if (myPartWheresDOM == null) {
        ReactDOM.render(
            <MyPartWheresModalBody
                partNo={partNo}
                colorId={colorId}
                setNo={setNo}
            />
            , document.getElementById("myModal-body")
        );
    } else {
        myPartWheresDOM.setState({
            partNo : partNo,
            colorId : colorId,
            setNo : setNo
        });
        myPartWheresDOM.loadMyPartWheresSimilar(partNo, colorId);
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
        this.loadMyPartWheresSimilar(this.state.partNo, this.state.colorId);
    }

    componentWillUnmount() {
        myPartWheresDOM = null;
    }

    loadMyPartWheresSimilar(partNo, colorId) {
        $.ajax({
            url:"/admin/myPartWheresSimilar",
            type : "GET",
            dataType : "json",
            data : {
                partNo : partNo,
                colorId : colorId
            },
            contentType: "application/json;charset=UTF-8",
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
                    partNo : {this.state.partNo}, colorId : {this.state.colorId}, setNo : {this.state.setNo}
                    <table className="table table-bordered">
                        <thead>
                        <tr>
                            <th>img</th>
                            <th>itemNo</th>
                            <th>where</th>
                            <th>qty</th>
                        </tr>
                        </thead>
                        <tbody>
                        {this.state.myItemWheres != null && this.state.myItemWheres.map(function(whereInfo, key) {
                            return <tr key={key}>
                                <td></td>
                                <td>
                                    {whereInfo.itemNo}
                                </td>
                                <td bgcolor={setNo == whereInfo.whereMore && 'f7d117'}>{whereInfo.whereCode} - {whereInfo.whereMore}</td>
                                <td>{whereInfo.qty}</td>
                            </tr>;
                        })}
                        </tbody>
                    </table>
                </div>
            </div>
        );
    }
}


