function addMyPartInCategoryModal(blCategoryId, partNo, e) {
    if (typeof e != "undefined") e.preventDefault();

    $('#myModal .modal-title').html("카테고리 부품 등록하기")
    $('#myModal').modal('toggle');

    if (myPartDOM == null) {
        ReactDOM.render(
            <MyPartModalBody blCategoryId={blCategoryId} partNo={partNo}/>
            , document.getElementById("myModal-body")
        );
    } else {
        myPartDOM.setState({
            blCategoryId : blCategoryId,
            partNo : partNo,
            categoryInfo : null,
            partInfo : null,
            allColorPartImgUrls : null,
            colorId : null,
            whereInfos : null,
            whereCode : "storage",
            whereMore : "storage",
            qty : null
        });
        myPartDOM.loadPartCategoryInfo(blCategoryId);
        myPartDOM.loadPartInfo(partNo);
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
            colorId : null,
            whereInfos : null,
            whereCode : "storage",
            whereMore : "storage",
            qty : null
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

    loadWhereInfos(partNo, colorId) {
        $.ajax({
            url:"/admin/myPartWhereInfos",
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
                whereInfos : data
            });
            data.forEach(function(whereInfo) {
                if (whereInfo.whereCode == "storage") {
                    pickMyPartWhere(whereInfo);
                }
            });
        }.bind(this));
    }

    increaseQty(val) {
        var qty = this.state.qty + val;
        if (qty <= 0) qty = 0;
        this.setState({qty : qty});
    }

    onchangeQty(qty) {
        this.setState({qty : qty});
    }

    saveQty() {
        $.ajax({
            url:"/admin/myPartQty",
            type : "POST",
            dataType : "json",
            data : {
                partNo : this.state.partNo,
                colorId : this.state.colorId,
                whereCode : this.state.whereCode,
                whereMore : this.state.whereMore,
                qty : this.state.qty
            },
            contentType: "application/x-www-form-urlencoded; charset=UTF-8",
            async : true
        }).done(function(data) {
            this.setState({
                partNo : data.itemNo,
                colorId : data.colorId,
                whereCode : data.whereCode,
                whereMore : data.whereMore,
                whereQty : data.whereQty
            });
            $("#saveMyPartQtyBtn").removeClass("btn-danger");
            $("#saveMyPartQtyBtn").addClass("btn-default");
            this.loadWhereInfos(data.itemNo, data.colorId);
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
                        <WhereInfos partInfo={this.state.partInfo} colorId={this.state.colorId} whereInfos={this.state.whereInfos} />
                        <MyPartQty partInfo={this.state.partInfo} colorId={this.state.colorId} whereCode={this.state.whereCode} whereMore={this.state.whereMore} qty={this.state.qty} />
                    </div>
                </form>
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
            <label><a href={'https://www.bricklink.com/v2/catalog/catalogitem.page?id=' + props.partInfo.id + '#T=C'} target={'_blank'}>부품 - partNo : {props.partInfo.partNo}, partName : {props.partInfo.partName}</a></label><br/>
            {/*<img src={props.partInfo.img}/>*/}
        </div>
    );
}

function ColorInfos(props) {
    if (props.partInfo == null || props.allColorPartImgUrls == null) return '';

    return (
        <div>
            <label><a href={'https://www.bricklink.com/catalogColors.asp?sortBy=N'} target={'_blank'}>색상 선택</a></label><br/>
            {props.allColorPartImgUrls.map(function(colorPartImgUrl, key) {
                return (
                    <a onClick={(e) => pickMyPartColor(props.partInfo.partNo, colorPartImgUrl.colorId, e)}>
                        <img key={key} name="colorPartImgUrl" id={'colorPartImgUrl_' + colorPartImgUrl.colorId} src={colorPartImgUrl.imgUrl} />
                        <span>{colorPartImgUrl.colorName}</span>
                    </a>
                );
            })}
            <input id={'myPartColorId'} type={'text'} value={myPartDOM.state.colorId} onChange={(e) => pickMyPartColor(props.partInfo.partNo, e.target.value, e)}/>&nbsp;&nbsp;
        </div>
    );
}

function pickMyPartColor(partNo, colorId, e) {
    if (typeof e != "undefined") e.preventDefault();

    $("[name=colorPartImgUrl]").css("border", "")
    $("#colorPartImgUrl_" + colorId).css("border", "2px solid rgb(255,0,0)");
    myPartDOM.setState({colorId : colorId});
    myPartDOM.loadWhereInfos(partNo, colorId);
}

function WhereInfos(props) {
    if (props.partInfo == null || props.colorId == null || props.whereInfos == null) return '';

    // [{"itemType":"P","itemNo":"3062b","colorId":"1","whereCode":"storage","whereMore":"storage","qty":0},{"itemType":"P","itemNo":"3062b","colorId":"1","whereCode":"wanted","whereMore":"11902","qty":4},{"itemType":"P","itemNo":"3062b","colorId":"1","whereCode":"wanted","whereMore":"2505","qty":15},{"itemType":"P","itemNo":"3062b","colorId":"1","whereCode":"wanted","whereMore":"3187","qty":4},{"itemType":"P","itemNo":"3062b","colorId":"1","whereCode":"wanted","whereMore":"60047","qty":2},{"itemType":"P","itemNo":"3062b","colorId":"1","whereCode":"wanted","whereMore":"7498","qty":2},{"itemType":"P","itemNo":"3062b","colorId":"1","whereCode":"wanted","whereMore":"75149","qty":1},{"itemType":"P","itemNo":"3062b","colorId":"1","whereCode":"wanted","whereMore":"7744","qty":10},{"itemType":"P","itemNo":"3062b","colorId":"1","whereCode":"wanted","whereMore":"8781","qty":2}]
    return (
        <div>
            <label>보관 위치</label><br/>
            {props.whereInfos.map(function(whereInfo, key) {
                return (
                    <button key={key} name="partWhereInfo" id={'partWhereInfo_' + whereInfo.whereCode + "_" + whereInfo.whereMore}
                            className={'btn btn-default'} onClick={(e) => pickMyPartWhere(whereInfo, e)}>{whereInfo.whereCode} - {whereInfo.whereMore} ({whereInfo.qty})</button>
                );
            })}

        </div>
    );
}

function pickMyPartWhere(whereInfo, e) {
    if (typeof e != "undefined") e.preventDefault();
    console.log("pickMyPartWhere - whereInfo : " + whereInfo);
    console.log("#partWhereInfo_" + whereInfo.whereCode + "_" + whereInfo.whereMore);

    $("[name=partWhereInfo]").removeClass("btn-info");
    $("[name=partWhereInfo]").addClass("btn-default");
    $("#partWhereInfo_" + whereInfo.whereCode + "_" + whereInfo.whereMore).addClass("btn-info");
    myPartDOM.setState({
        whereCode : whereInfo.whereCode,
        whereMore : whereInfo.whereMore,
        qty : whereInfo.qty
    });
}

function MyPartQty(props) {
    if (props.partInfo == null || props.colorId == null || props.qty == null) return '';

    return (
        <div>
            <label>수량</label><br/>
            <input id={'myPartQty'} type={'text'} value={props.qty} onChange={(e) => onchangeMyPartQty(e.target.value, e)}/>&nbsp;&nbsp;
            <button className={'btn btn-lg btn-default'} onClick={(e) => increaseMyPartQty(e)}>+</button>&nbsp;&nbsp;
            <button className={'btn btn-lg btn-default'} onClick={(e) => increaseMyPartQtyX5(e)}>+5</button>&nbsp;&nbsp;
            <button className={'btn btn-lg btn-default'} onClick={(e) => decreaseMyPartQty(e)}>-</button>&nbsp;&nbsp;
            <button id={'saveMyPartQtyBtn'} className={'btn btn-lg btn-default'} onClick={(e) => saveMyPartQty(e)}>SAVE</button>
        </div>
    );
}

function onchangeMyPartQty(qty, e) {
    if (typeof e != "undefined") e.preventDefault();
    myPartDOM.onchangeQty(qty);
    $("#saveMyPartQtyBtn").removeClass("btn-default");
    $("#saveMyPartQtyBtn").addClass("btn-danger");
}

function increaseMyPartQty(e) {
    if (typeof e != "undefined") e.preventDefault();
    myPartDOM.increaseQty(1);
    $("#saveMyPartQtyBtn").removeClass("btn-default");
    $("#saveMyPartQtyBtn").addClass("btn-danger");
}

function increaseMyPartQtyX5(e) {
    if (typeof e != "undefined") e.preventDefault();
    myPartDOM.increaseQty(5);
    $("#saveMyPartQtyBtn").removeClass("btn-default");
    $("#saveMyPartQtyBtn").addClass("btn-danger");
}

function decreaseMyPartQty(e) {
    if (typeof e != "undefined") e.preventDefault();
    myPartDOM.increaseQty(-1);
    $("#saveMyPartQtyBtn").removeClass("btn-default");
    $("#saveMyPartQtyBtn").addClass("btn-danger");
}

function saveMyPartQty(e) {
    if (typeof e != "undefined") e.preventDefault();
    myPartDOM.saveQty();
}

