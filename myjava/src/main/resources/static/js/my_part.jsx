function newMyPartModal(blCategoryId, partNo, e) {
    if (typeof e != "undefined") e.preventDefault();

    $('#myModal .modal-title').html("부품 등록하기")
    $('#myModal').modal('toggle');

    if (myPartDOM == null) {
        ReactDOM.render(
            <MyPartModalBody blCategoryId={blCategoryId} partNo={partNo}/>
            , document.getElementById("myModal-body")
        );
    } else {
        myPartDOM.setState({
            blCategoryId : blCategoryId,
            partNo : partNo
        });
        myPartDOM.loadPartCategoryInfo(blCategoryId);
    }
}

var myPartDOM = null;
class MyPartModalBody extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            blCategoryId : props.blCategoryId,
            partNo : props.partNo,
            categoryInfo : null,
            partInfo : null,
            allColorPartImgUrls : null,
            colorId : null
        };
    }

    setState(state) {
        super.setState(state);
    }

    componentDidMount() {
        myPartDOM = this;
        this.loadPartCategoryInfo(this.state.blCategoryId);
        this.loadPartInfo(this.state.partNo);
    }

    componentWillUnmount() {
        myPartDOM = null;
    }

    loadPartCategoryInfo(blCategoryId) {
        $.ajax({
            url:"/partCategory",
            type : "GET",
            dataType : "json",
            data : {blCategoryId : blCategoryId},
            contentType: "application/json;charset=UTF-8",
            async : true
        }).done(function(data) {
            this.setState({
                categoryInfo : data
            });
        }.bind(this));
    }

    loadPartInfo(partNo) {
        $.ajax({
            url:"/partByNo",
            type : "GET",
            dataType : "json",
            data : {partNo : partNo},
            contentType: "application/json;charset=UTF-8",
            async : true
        }).done(function(data) {
            this.setState({
                partInfo : data
            });
            this.loadColorIds(this.state.partNo);
        }.bind(this));
    }

    loadColorIds(partNo) {
        $.ajax({
            url:"/allColorPartImgUrlsByPartNo",
            type : "GET",
            dataType : "json",
            data : {partNo : partNo},
            contentType: "application/json;charset=UTF-8",
            async : true
        }).done(function(data) {
            this.setState({
                allColorPartImgUrls : data
            });
        }.bind(this));
    }

    render() {
        return (
            <div>
                <form id={"partCategoryForm"}>
                    <div className={"form-group"}>
                        <MyPartCategoryInfo categoryInfo={this.state.categoryInfo} />
                        <MyPartInfo partInfo={this.state.partInfo} />
                        <ColorInfos partInfo={this.state.partInfo} allColorPartImgUrls={this.state.allColorPartImgUrls} />
                        <WhereInfos partInfo={this.state.partInfo} colorId={this.state.colorId} />
                    </div>
                </form>
                <button type={"submit"} className={"btn btn-primary"} onClick={(e) => newPartCategory($("#partCategoryForm"), e)}>생성하기</button>
            </div>
        );
    }
}

function MyPartCategoryInfo(props) {
    if (props.categoryInfo == null) return '';

    var repImgs = null;
    if (props.categoryInfo.repImgs !== undefined) {
        repImgs = JSON.parse(props.categoryInfo.repImgs);
    }
    // {"id":8,"blCategoryId":8,"type":"P","name":"Brick, Round","parts":50,"depth":1,"parentId":217,"repImgs":"[\"http://img.bricklink.com/ItemImage/PT/5/3062b.t1.png\",\"http://img.bricklink.com/ItemImage/PT/7/3941.t1.png\",\"http://img.bricklink.com/ItemImage/PT/48/85080.t1.png\",\"http://img.bricklink.com/ItemImage/PT/88/92947.t1.png\",\"http://img.bricklink.com/ItemImage/PT/1/3063.t1.png\",\"http://img.bricklink.com/ItemImage/PT/153/48092.t1.png\",\"http://img.bricklink.com/ItemImage/PT/1/3062a.t1.png\"]","setQty":23051,"sortOrder":0}
    return (
        <div>
            <label>카테고리 - blCategoryId : {props.categoryInfo.blCategoryId}, name : {props.categoryInfo.name}</label><br/>
            {repImgs != null ? repImgs.map(function(repImg, imgKey) {
                return <img src={repImg} key={imgKey}/>
            }) : ''}
        </div>
    );
}

function MyPartInfo(props) {
    if (props.partInfo == null) return '';

    // {"id":"444","categoryId":8,"img":"http://img.bricklink.com/ItemImage/PT/5/3062b.t1.png","partNo":"3062b","partName":"Brick, Round 1 x 1 Open Stud","setQty":9841,"myItemsQty":0}
    return (
        <div>
            <label>부품 - partNo : {props.partInfo.partNo}, partName : {props.partInfo.partName}</label><br/>
            {/*<img src={props.partInfo.img}/>*/}
        </div>
    );
}

function ColorInfos(props) {
    if (props.partInfo == null || props.allColorPartImgUrls == null) return '';

    return (
        <div>
            <label>색상 선택</label><br/>
            {props.allColorPartImgUrls.map(function(colorPartImgUrl, key) {
                return <img key={key} name="colorPartImgUrl" id={'colorPartImgUrl_' + colorPartImgUrl.colorId} src={colorPartImgUrl.imgUrl} onClick={(e) => pickMyPartColor(colorPartImgUrl.colorId, e)}/>
            })}
        </div>
    );
}

function pickMyPartColor(colorId, e) {
    if (typeof e != "undefined") e.preventDefault();

    console.log("pickMyPartColor : " + colorId);
    $("[name=colorPartImgUrl]").css("border", "")
    $("#colorPartImgUrl_" + colorId).css("border", "2px solid rgb(255,0,0)");
    myPartDOM.setState({colorId : colorId});
}

function WhereInfos(props) {
    if (props.partInfo == null || props.colorId == null) return '';

    return (
        <div>
            <label>보관 위치</label><br/>
            colorId : {props.colorId}
        </div>
    );
}

